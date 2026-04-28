package com.moesegfault.banking.application.customer.command;

import com.moesegfault.banking.domain.customer.Address;
import com.moesegfault.banking.domain.customer.IdentityDocument;
import com.moesegfault.banking.domain.customer.IdentityDocumentType;
import com.moesegfault.banking.domain.customer.PhoneNumber;
import com.moesegfault.banking.domain.customer.TaxProfile;
import java.util.Objects;

/**
 * @brief 注册客户命令（Register Customer Command），封装 customer register 用例输入；
 *        Register customer command that encapsulates customer-register use-case inputs.
 */
public final class RegisterCustomerCommand {

    /**
     * @brief 证件类型（Identity Document Type）；
     *        Identity document type.
     */
    private final IdentityDocumentType idType;

    /**
     * @brief 证件号码（Identity Document Number）；
     *        Identity document number.
     */
    private final String idNumber;

    /**
     * @brief 签发地区（Issuing Region）；
     *        Identity-document issuing region.
     */
    private final String issuingRegion;

    /**
     * @brief 手机号（Mobile Phone）；
     *        Mobile phone number.
     */
    private final String mobilePhone;

    /**
     * @brief 居住地址（Residential Address）；
     *        Residential address.
     */
    private final String residentialAddress;

    /**
     * @brief 通信地址（Mailing Address）；
     *        Mailing address.
     */
    private final String mailingAddress;

    /**
     * @brief 是否美国税务居民（US Tax Resident Flag）；
     *        Whether customer is a US tax resident.
     */
    private final boolean usTaxResident;

    /**
     * @brief CRS 信息（CRS Information）；
     *        CRS information text, nullable.
     */
    private final String crsInfo;

    /**
     * @brief 构造注册客户命令（Construct Register Customer Command）；
     *        Construct register-customer command.
     *
     * @param idType             证件类型（Identity document type）。
     * @param idNumber           证件号码（Identity document number）。
     * @param issuingRegion      签发地区（Issuing region）。
     * @param mobilePhone        手机号（Mobile phone）。
     * @param residentialAddress 居住地址（Residential address）。
     * @param mailingAddress     通信地址（Mailing address）。
     * @param usTaxResident      是否美国税务居民（US tax resident flag）。
     * @param crsInfo            CRS 信息（CRS info, nullable）。
     */
    public RegisterCustomerCommand(
            final IdentityDocumentType idType,
            final String idNumber,
            final String issuingRegion,
            final String mobilePhone,
            final String residentialAddress,
            final String mailingAddress,
            final boolean usTaxResident,
            final String crsInfo
    ) {
        this.idType = Objects.requireNonNull(idType, "Identity document type must not be null");
        this.idNumber = Objects.requireNonNull(idNumber, "Identity document number must not be null");
        this.issuingRegion = Objects.requireNonNull(issuingRegion, "Issuing region must not be null");
        this.mobilePhone = Objects.requireNonNull(mobilePhone, "Mobile phone must not be null");
        this.residentialAddress = Objects.requireNonNull(
                residentialAddress,
                "Residential address must not be null");
        this.mailingAddress = Objects.requireNonNull(mailingAddress, "Mailing address must not be null");
        this.usTaxResident = usTaxResident;
        this.crsInfo = crsInfo;
    }

    /**
     * @brief 返回证件类型（Return Identity Document Type）；
     *        Return identity document type.
     *
     * @return 证件类型（Identity document type）。
     */
    public IdentityDocumentType idType() {
        return idType;
    }

    /**
     * @brief 返回证件号码（Return Identity Document Number）；
     *        Return identity document number.
     *
     * @return 证件号码（Identity document number）。
     */
    public String idNumber() {
        return idNumber;
    }

    /**
     * @brief 返回签发地区（Return Issuing Region）；
     *        Return issuing region.
     *
     * @return 签发地区（Issuing region）。
     */
    public String issuingRegion() {
        return issuingRegion;
    }

    /**
     * @brief 返回手机号（Return Mobile Phone）；
     *        Return mobile phone.
     *
     * @return 手机号（Mobile phone）。
     */
    public String mobilePhone() {
        return mobilePhone;
    }

    /**
     * @brief 返回居住地址（Return Residential Address）；
     *        Return residential address.
     *
     * @return 居住地址（Residential address）。
     */
    public String residentialAddress() {
        return residentialAddress;
    }

    /**
     * @brief 返回通信地址（Return Mailing Address）；
     *        Return mailing address.
     *
     * @return 通信地址（Mailing address）。
     */
    public String mailingAddress() {
        return mailingAddress;
    }

    /**
     * @brief 返回美国税务居民标记（Return US Tax Resident Flag）；
     *        Return US tax resident flag.
     *
     * @return 美国税务居民返回 true（true when US tax resident）。
     */
    public boolean usTaxResident() {
        return usTaxResident;
    }

    /**
     * @brief 返回 CRS 信息（Return CRS Information）；
     *        Return CRS information text.
     *
     * @return CRS 信息（CRS information, nullable）。
     */
    public String crsInfo() {
        return crsInfo;
    }

    /**
     * @brief 转换为身份证件值对象（Map to Identity Document）；
     *        Map command fields to identity-document value object.
     *
     * @return 身份证件值对象（Identity document value object）。
     */
    public IdentityDocument toIdentityDocument() {
        return IdentityDocument.of(idType, idNumber, issuingRegion);
    }

    /**
     * @brief 转换为手机号值对象（Map to Phone Number）；
     *        Map command field to phone-number value object.
     *
     * @return 手机号值对象（Phone number value object）。
     */
    public PhoneNumber toPhoneNumber() {
        return PhoneNumber.of(mobilePhone);
    }

    /**
     * @brief 转换为居住地址值对象（Map to Residential Address）；
     *        Map command field to residential address value object.
     *
     * @return 居住地址值对象（Residential address value object）。
     */
    public Address toResidentialAddress() {
        return Address.of(residentialAddress);
    }

    /**
     * @brief 转换为通信地址值对象（Map to Mailing Address）；
     *        Map command field to mailing address value object.
     *
     * @return 通信地址值对象（Mailing address value object）。
     */
    public Address toMailingAddress() {
        return Address.of(mailingAddress);
    }

    /**
     * @brief 转换为税务资料值对象（Map to Tax Profile）；
     *        Map command fields to tax-profile value object.
     *
     * @return 税务资料值对象（Tax profile value object）。
     */
    public TaxProfile toTaxProfile() {
        return TaxProfile.of(usTaxResident, crsInfo);
    }
}
