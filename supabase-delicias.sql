--------------------------------------------------
-- Restaurants
--------------------------------------------------

create table restaurants (

  id numeric primary key,
  name text not null,
  logo_url text not null,
  address text,
  rating numeric null
);


--------------------------------------------------
-- Deliverers
--------------------------------------------------

create table deliverers (
  id uuid primary key,
  name text not null,
  logo_url text null,
  rating numeric null
);

--------------------------------------------------
-- Orders
--------------------------------------------------

create table orders (

    id numeric primary key,

    user_id uuid not null, -- keycloak user delicias app

    restaurant_id numeric references restaurants(id) on delete cascade,

    delivery_id uuid null references deliverers(id) on delete cascade,

    status text check (
        status in (
        'ORDERED',
        'ACCEPTED',
        'COOKING',
        'READY_FOR_DELIVERY',
        'DELIVERY_ASSIGNED_ORDER',
        'DELIVERY_ROAD_TO_STORE',
        'DELIVERY_ROAD_TO_DESTINATION',
        'DELIVERED',
        'CANCELLED',
        'REJECTED',
        'READY_FOR_PICKUP'
        )
    ) not null default 'ORDERED',

    created_at timestamp with time zone default now(),
    updated_at timestamp with time zone default now()
);


-- Para listar rápido los pedidos por estatus (Kanban)
create index idx_orders_status on orders (status);

-- Para obtener pedidos de un usuario (cliente) filtrando por estatus
create index idx_orders_user_status on orders (user_id, status);

-- Para obtener pedidos de un restaurante filtrando por estatus
create index idx_orders_restaurante_status on orders (restaurant_id, status);

-- Para obtener pedidos de un repartidor filtrando por estatus
create index idx_orders_delivery_status on orders (delivery_id, status);

-- Índice parcial para acelerar solo los pedidos "activos" (no entregados)
create index idx_orders_actives on orders (status) where status != 'DELIVERED';

---


--------------------------------------------------
-- Order Line
--------------------------------------------------

create table order_line (
  id numeric primary key,
  order_id numeric not null references orders(id) on delete cascade,
  product_name text not null,
  quantity numeric not null,
  price numeric not null,
  description text not null
);

--------------------------------------------------
-- Permissons Bucket
--------------------------------------------------

-- Permitir leer archivos
create policy "Public Access Read"
on storage.objects
for select
using (bucket_id = 'delicias');

-- Permitir subir archivos
create policy "Public Access Upload"
on storage.objects
for insert
with check (bucket_id = 'delicias');
