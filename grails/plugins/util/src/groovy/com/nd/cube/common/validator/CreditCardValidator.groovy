package com.nd.cube.common.validator

/**
 * @author wuzj
 * @since 2015/1/6.
 */
class CreditCardValidator extends AbstractValidator{
    Boolean creditCard
    String defaultMessage = "\\u7684\\u503C{2}\\u4E0D\\u662F\\u4E00\\u4E2A\\u6709\\u6548\\u7684\\u4FE1\\u7528\\u5361\\u53F7"
    CreditCardValidator(Boolean creditCard){
        this.creditCard = creditCard
    }

    @Override
    Boolean validate(Object target) {
        org.apache.commons.validator.CreditCardValidator commonCreditCard = new org.apache.commons.validator.CreditCardValidator()
        if (target instanceof String && !creditCard || commonCreditCard.isValid((String)target)){
            return true
        }
        return false
    }


}
