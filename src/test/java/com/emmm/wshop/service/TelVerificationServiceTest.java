package com.emmm.wshop.service;

import com.emmm.wshop.controller.AuthController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TelVerificationServiceTest {
    private static AuthController.TelAndCode VALID_PARAMETER = new AuthController.TelAndCode("13111111111", null);
    private static AuthController.TelAndCode VALID_PARAMETER_CODE = new AuthController.TelAndCode("13111111111", "000000");
    private static AuthController.TelAndCode EMPTY_TEL = new AuthController.TelAndCode(null, null);

    public static AuthController.TelAndCode getValidParameter() {
        return VALID_PARAMETER;
    }

    public static AuthController.TelAndCode getEmptyTel() {
        return EMPTY_TEL;
    }

    @Test
    void returnTrueIfValid() {
        Assertions.assertTrue(new TelVerificationService()
                .verifyTelParameter(VALID_PARAMETER));
    }

    @Test
    void returnFalseIfNoTel() {
        Assertions.assertFalse(new TelVerificationService()
                .verifyTelParameter(EMPTY_TEL));
    }

    @Test
    void returnFalseIfParamNull() {
        Assertions.assertFalse(new TelVerificationService()
                .verifyTelParameter(null));
    }
}
