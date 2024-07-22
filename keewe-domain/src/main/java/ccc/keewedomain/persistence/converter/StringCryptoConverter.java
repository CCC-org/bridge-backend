package ccc.keewedomain.persistence.converter;

import ccc.keewecore.utils.AESEncryption;
import lombok.RequiredArgsConstructor;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
@RequiredArgsConstructor
public class StringCryptoConverter implements AttributeConverter<String, String> {

    private final AESEncryption aesEncryption;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }

        try {
            return aesEncryption.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        try {
            return aesEncryption.decrypt(dbData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
