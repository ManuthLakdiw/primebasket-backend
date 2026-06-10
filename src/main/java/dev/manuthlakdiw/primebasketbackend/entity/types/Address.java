package dev.manuthlakdiw.primebasketbackend.entity.types;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.io.Serializable;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address implements Serializable {

   @Enumerated(EnumType.STRING)
   private AddressType addressType;
   private String street;
   private String city;
   private String district;
   private String postalCode;
}
