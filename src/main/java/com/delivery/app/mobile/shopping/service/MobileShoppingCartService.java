package com.delivery.app.mobile.shopping.service;

import com.delivery.app.configs.DeliciasAppProperties;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.mobile.shopping.dto.DeliveryChargesDTO;
import com.delivery.app.mobile.shopping.dto.MobileShoppingCartAvailableDTO;
import com.delivery.app.mobile.shopping.dto.MobileShoppingCartDTO;
import com.delivery.app.mobile.shopping.model.ShoppingCart;
import com.delivery.app.mobile.shopping.model.ShoppingCartLine;
import com.delivery.app.mobile.shopping.repository.ShoppingCartRepository;
import com.delivery.app.pos.enums.AdjustmentType;
import com.delivery.app.product.attribute.models.ProductAttributeValue;
import com.delivery.app.security.model.UserAddress;
import com.delivery.app.security.repository.UserAddressRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.delivery.app.security.enums.UserAddressType.OFFICE;

@AllArgsConstructor
@Service
public class MobileShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final DeliciasAppProperties deliciasAppProperties;
    private final MobileShoppingCartChargesService mobileShoppingCartChargesService;
    private final UserAddressRepository userAddressRepository;


    public List<MobileShoppingCartAvailableDTO> availableShoppingCart() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userUid = UUID.fromString(authentication.getName());

        return shoppingCartRepository.findByUserUID(userUid)
                .stream().filter(item -> !item.getLines().isEmpty())
                .map(cart -> MobileShoppingCartAvailableDTO.builder()
                        .id(cart.getId())
                        .restaurantName(cart.getRestaurant().getName())
                        .restaurantLogo(Optional.ofNullable(cart.getRestaurant().getImageLogo())
                                .orElse(deliciasAppProperties.getSupabase().getLogo()))
                        .lineCount(cart.getLines().size())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public MobileShoppingCartDTO findById(UUID shoppingCartId) {

        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> new ResourceNotFoundException("ShoppingCart", "id", shoppingCartId));

        double subtotal = 0.0;

        List<MobileShoppingCartDTO.ShoppingCharge> charges = new ArrayList<>();
        List<MobileShoppingCartDTO.ShoppingLine> lines = new ArrayList<>();

        for (ShoppingCartLine line : shoppingCart.getLines()) {

            // calculate total attr values
            double totalAmountAttrValues = 0.0;

            totalAmountAttrValues += line.getProductTemplate().getAttributeValues()
                    .stream()
                    .filter(attrVal -> line.getAttrValuesIds().contains(attrVal.getId()))
                    .map(i -> Optional.ofNullable(i.getExtraPrice()).orElse(0.0))
                    .reduce(0.0, Double::sum) * line.getQty();

            double amountLineAdded = totalAmountAttrValues + (line.getQty() * line.getProductTemplate().getListPrice());

            // get attributes added
            List<Integer> attrValuesAdded = line.getProductTemplate().getAttributeValues()
                    .stream()
                    .map(ProductAttributeValue::getId)
                    .filter(id -> line.getAttrValuesIds().contains(id))
                    .toList();

            subtotal += amountLineAdded;

            lines.add(MobileShoppingCartDTO.ShoppingLine.builder()
                    .id(line.getId())
                    .priceUnit(line.getProductTemplate().getListPrice())
                    .priceTotal(amountLineAdded)
                    .qty(line.getQty())
                    .productTmplId(line.getProductTemplate().getId())
                    .productTmplName(line.getProductTemplate().getName())
                    .productTmplDescription(line.getProductTemplate().getDescription())
                    .attrValuesAdded(attrValuesAdded)
                    .build());

        }

        boolean hasDeliveryAddress = Optional.ofNullable(shoppingCart.getUserAddress()).isPresent();

        if (hasDeliveryAddress) {

            DeliveryChargesDTO deliveryCharges = mobileShoppingCartChargesService.getDeliveryCharges(shoppingCart.getRestaurant().getId(), shoppingCart.getUserAddress().getId());

            charges.add(MobileShoppingCartDTO.ShoppingCharge.builder()
                            .adjustmentType(AdjustmentType.CHARGE)
                            .name("Costo de envío")
                            .amount(deliveryCharges.shipmentCost())
                    .build());

            /*charges.add(MobileShoppingCartDTO.ShoppingCharge.builder()
                    .name("Cupón de descuento")
                    .amount(-15.00)
                    .build());
                    */
        }

        double totalCharges = charges.stream()
                .map(MobileShoppingCartDTO.ShoppingCharge::amount)
                .reduce(0.0, Double::sum);

        return MobileShoppingCartDTO.builder()
                .id(shoppingCart.getId())
                .restaurantId(shoppingCart.getRestaurant().getId())
                .shoppingLines(lines)
                .subtotal(subtotal)
                .total(subtotal + totalCharges)
                .hasDeliveryAddress(hasDeliveryAddress)
                .charges(charges)
                .deliveryAddress(hasDeliveryAddress
                        ? MobileShoppingCartDTO.ShoppingCartDeliveryAddress.builder()
                        .id(shoppingCart.getUserAddress().getId())
                        .address(shoppingCart.getUserAddress().getAddress())
                        .name(shoppingCart.getUserAddress().getAddressType().equals(OFFICE)
                                ? shoppingCart.getUserAddress().getCompanyName()
                                : shoppingCart.getUserAddress().getDetails()
                        )
                        .icon(switch (shoppingCart.getUserAddress().getAddressType()) {
                            case HOME -> "assets/fd/home.svg";
                            case DEPTO -> "assets/fd/depto.svg";
                            case OFFICE -> "assets/fd/office.svg";
                            case OTHER -> "assets/fd/other.svg";
                        })
                        .latitude(shoppingCart.getUserAddress().getPosition().getY())
                        .longitude(shoppingCart.getUserAddress().getPosition().getX())
                        .build()
                        : null
                )
                .build();
    }


    @Transactional
    public void setDeliveryAddress(UUID shoppingCartId, Integer userAddressId) {

        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> new ResourceNotFoundException("ShoppingCart", "id", shoppingCartId));

        UserAddress userAddress = userAddressRepository.findById(userAddressId)
                .orElseThrow(() -> new ResourceNotFoundException("ShoppingCart", "id", shoppingCartId));

        shoppingCart.setUserAddress(userAddress);

    }
}