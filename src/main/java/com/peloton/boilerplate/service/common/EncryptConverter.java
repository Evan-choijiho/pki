package com.peloton.boilerplate.service.common;

import com.peloton.boilerplate.util.AESEncryptionUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EncryptConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            return (attribute == null) ? null : AESEncryptionUtil.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException("column data encrypt fail", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            return (dbData == null) ? null : AESEncryptionUtil.decrypt(dbData);
        } catch (Exception e) {
            throw new RuntimeException("column data decrypt fail", e);
        }
    }
}
