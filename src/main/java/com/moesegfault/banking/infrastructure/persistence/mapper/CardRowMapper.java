package com.moesegfault.banking.infrastructure.persistence.mapper;

import com.moesegfault.banking.domain.card.CardExpiry;
import com.moesegfault.banking.domain.card.CardId;
import com.moesegfault.banking.domain.card.CardNumber;
import com.moesegfault.banking.domain.card.CardRole;
import com.moesegfault.banking.domain.card.CardStatus;
import com.moesegfault.banking.domain.card.CreditCard;
import com.moesegfault.banking.domain.card.CreditCardAccountId;
import com.moesegfault.banking.domain.card.CustomerId;
import com.moesegfault.banking.domain.card.DebitCard;
import com.moesegfault.banking.domain.card.DebitCardBinding;
import com.moesegfault.banking.domain.card.FxAccountId;
import com.moesegfault.banking.domain.card.SavingsAccountId;
import com.moesegfault.banking.domain.card.SupplementaryDebitCard;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import org.springframework.jdbc.core.RowMapper;

/**
 * @brief 卡片行映射器（Card Row Mapper）；
 *        Maps card related rows to domain objects.
 */
public final class CardRowMapper {

        /**
         * @brief 借记卡映射器（Debit Card Mapper）；
         *        Mapper for `debit_card` records.
         */
        public static final RowMapper<DebitCard> DEBIT_CARD = (resultSet, rowNum) -> DebitCard.restore(
                        CardId.of(resultSet.getString("card_id")),
                        CardNumber.of(resultSet.getString("card_no")),
                        CustomerId.of(resultSet.getString("holder_customer_id")),
                        DebitCardBinding.of(
                                        SavingsAccountId.of(resultSet.getString("savings_account_id")),
                                        FxAccountId.of(resultSet.getString("fx_account_id"))),
                        CardStatus.valueOf(resultSet.getString("card_status")),
                        CardExpiry.of(
                                        getInstant(resultSet, "issued_at"),
                                        getInstant(resultSet, "expired_at")));

        /**
         * @brief 借记附属卡映射器（Supplementary Debit Card Mapper）；
         *        Mapper for `supplementary_debit_card` records.
         */
        public static final RowMapper<SupplementaryDebitCard> SUPPLEMENTARY_DEBIT_CARD = (resultSet,
                        rowNum) -> SupplementaryDebitCard.restore(
                                        CardId.of(resultSet.getString("supplementary_card_id")),
                                        CardNumber.of(resultSet.getString("card_no")),
                                        CustomerId.of(resultSet.getString("holder_customer_id")),
                                        CardId.of(resultSet.getString("primary_debit_card_id")),
                                        CardStatus.valueOf(resultSet.getString("card_status")),
                                        CardExpiry.of(
                                                        getInstant(resultSet, "issued_at"),
                                                        getInstant(resultSet, "expired_at")));

        /**
         * @brief 信用卡映射器（Credit Card Mapper）；
         *        Mapper for `credit_card` records.
         */
        public static final RowMapper<CreditCard> CREDIT_CARD = (resultSet, rowNum) -> CreditCard.restore(
                        CardId.of(resultSet.getString("credit_card_id")),
                        CardNumber.of(resultSet.getString("card_no")),
                        CustomerId.of(resultSet.getString("holder_customer_id")),
                        CreditCardAccountId.of(resultSet.getString("credit_card_account_id")),
                        CardRole.fromDatabaseValue(resultSet.getString("card_role")),
                        nullableCardId(resultSet.getString("primary_credit_card_id")),
                        CardStatus.valueOf(resultSet.getString("card_status")),
                        CardExpiry.of(
                                        getInstant(resultSet, "issued_at"),
                                        getInstant(resultSet, "expired_at")));

        /**
         * @brief 禁止实例化工具类（Non-instantiable Utility Class）；
         *        Utility class should not be instantiated.
         */
        private CardRowMapper() {
        }

        /**
         * @brief 解析可空卡片 ID（Parse Nullable Card ID）；
         *        Parses nullable card identifier.
         *
         * @param rawValue 原始值（Raw value）。
         * @return 卡片 ID 或 null（Card ID or null）。
         */
        private static CardId nullableCardId(final String rawValue) {
                if (rawValue == null || rawValue.isBlank()) {
                        return null;
                }
                return CardId.of(rawValue);
        }

        /**
         * @brief 从结果集读取时间戳（Read Instant from ResultSet）；
         *        Reads nullable timestamp column as `Instant`.
         *
         * @param resultSet 结果集（Result set）。
         * @param column    列名（Column name）。
         * @return 时间点或 null（Instant or null）。
         * @throws SQLException SQL 读取异常（SQL read exception）。
         */
        private static Instant getInstant(final ResultSet resultSet, final String column) throws SQLException {
                final Timestamp timestamp = resultSet.getTimestamp(column);
                return timestamp == null ? null : timestamp.toInstant();
        }
}
