package com.moesegfault.banking.presentation.cli.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.moesegfault.banking.application.card.command.IssueCreditCardCommand;
import com.moesegfault.banking.application.card.command.IssueCreditCardHandler;
import com.moesegfault.banking.application.card.command.IssueDebitCardCommand;
import com.moesegfault.banking.application.card.command.IssueDebitCardHandler;
import com.moesegfault.banking.application.card.command.IssueSupplementaryCreditCardCommand;
import com.moesegfault.banking.application.card.command.IssueSupplementaryCreditCardHandler;
import com.moesegfault.banking.application.card.command.IssueSupplementaryDebitCardCommand;
import com.moesegfault.banking.application.card.command.IssueSupplementaryDebitCardHandler;
import com.moesegfault.banking.application.card.query.FindCardHandler;
import com.moesegfault.banking.application.card.query.FindCardQuery;
import com.moesegfault.banking.application.card.result.CardKind;
import com.moesegfault.banking.application.card.result.CardResult;
import com.moesegfault.banking.application.card.result.IssueCardResult;
import com.moesegfault.banking.presentation.cli.CommandRegistry;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @brief 卡 CLI 处理器单元测试（Card CLI Handlers Unit Test），验证命令参数映射、应用层调用与输出 schema；
 *        Unit tests for card CLI handlers, verifying command-option mapping, application invocation, and output schema.
 */
class CardCliHandlersTest {

    /**
     * @brief 验证主借记卡 handler 使用 schema 参数并调用应用层；
     *        Verify debit-card handler uses schema options and invokes application service.
     */
    @Test
    void shouldHandleIssueDebitCardCommandWithSchemaOptions() {
        final IssueDebitCardHandler applicationHandler = Mockito.mock(IssueDebitCardHandler.class);
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final PrintStream output = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        final IssueDebitCardCliHandler handler = new IssueDebitCardCliHandler(applicationHandler, output);

        when(applicationHandler.handle(any(IssueDebitCardCommand.class))).thenReturn(new IssueCardResult(
                "debit-card-001",
                "6222****5555",
                CardKind.DEBIT,
                "ACTIVE",
                "cust-001",
                Instant.parse("2026-04-28T08:00:00Z"),
                null));

        handler.handle(new ParsedCommand(
                "card issue-debit --holder_customer_id cust-001 --savings_account_id sav-001 --fx_account_id fx-001 --card_no 6222333344445555",
                List.of("card", "issue-debit"),
                Map.of(
                        "holder_customer_id", "cust-001",
                        "savings_account_id", "sav-001",
                        "fx_account_id", "fx-001",
                        "card_no", "6222333344445555")));

        final ArgumentCaptor<IssueDebitCardCommand> commandCaptor = ArgumentCaptor.forClass(IssueDebitCardCommand.class);
        verify(applicationHandler, times(1)).handle(commandCaptor.capture());

        final IssueDebitCardCommand command = commandCaptor.getValue();
        assertEquals("cust-001", command.holderCustomerId());
        assertEquals("sav-001", command.savingsAccountId());
        assertEquals("fx-001", command.fxAccountId());
        assertEquals("6222333344445555", command.cardNo());

        final String outputText = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(outputText.contains("card_id,masked_card_number,card_kind,card_status,holder_customer_id,issued_at,primary_card_id"));
        assertTrue(outputText.contains("debit-card-001,6222****5555,DEBIT,ACTIVE,cust-001,2026-04-28T08:00:00Z,"));
    }

    /**
     * @brief 验证借记附属卡 handler 支持连字符参数别名；
     *        Verify supplementary-debit handler supports hyphen-style option aliases.
     */
    @Test
    void shouldHandleIssueSupplementaryDebitCardWithHyphenOptions() {
        final IssueSupplementaryDebitCardHandler applicationHandler = Mockito.mock(IssueSupplementaryDebitCardHandler.class);
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final PrintStream output = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        final IssueSupplementaryDebitCardCliHandler handler =
                new IssueSupplementaryDebitCardCliHandler(applicationHandler, output);

        when(applicationHandler.handle(any(IssueSupplementaryDebitCardCommand.class))).thenReturn(new IssueCardResult(
                "sup-debit-001",
                "6222****7777",
                CardKind.SUPPLEMENTARY_DEBIT,
                "ACTIVE",
                "cust-002",
                Instant.parse("2026-04-28T09:00:00Z"),
                "debit-100"));

        handler.handle(new ParsedCommand(
                "card issue-supplementary-debit --holder-customer-id cust-002 --primary-debit-card-id debit-100 --card-no 6222000011117777",
                List.of("card", "issue-supplementary-debit"),
                Map.of(
                        "holder-customer-id", "cust-002",
                        "primary-debit-card-id", "debit-100",
                        "card-no", "6222000011117777")));

        verify(applicationHandler, times(1)).handle(any(IssueSupplementaryDebitCardCommand.class));
        final String outputText = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(outputText.contains("SUPPLEMENTARY_DEBIT"));
        assertTrue(outputText.contains("debit-100"));
    }

    /**
     * @brief 验证主信用卡 handler 使用 schema 参数并输出结果；
     *        Verify credit-card handler uses schema options and prints result.
     */
    @Test
    void shouldHandleIssueCreditCardCommand() {
        final IssueCreditCardHandler applicationHandler = Mockito.mock(IssueCreditCardHandler.class);
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final PrintStream output = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        final IssueCreditCardCliHandler handler = new IssueCreditCardCliHandler(applicationHandler, output);

        when(applicationHandler.handle(any(IssueCreditCardCommand.class))).thenReturn(new IssueCardResult(
                "credit-card-001",
                "4111****4444",
                CardKind.PRIMARY_CREDIT,
                "ACTIVE",
                "cust-003",
                Instant.parse("2026-04-28T10:00:00Z"),
                null));

        handler.handle(new ParsedCommand(
                "card issue-credit --holder_customer_id cust-003 --credit_card_account_id cc-acc-001 --card_no 4111222233334444",
                List.of("card", "issue-credit"),
                Map.of(
                        "holder_customer_id", "cust-003",
                        "credit_card_account_id", "cc-acc-001",
                        "card_no", "4111222233334444")));

        verify(applicationHandler, times(1)).handle(any(IssueCreditCardCommand.class));
        final String outputText = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(outputText.contains("credit-card-001"));
        assertTrue(outputText.contains("PRIMARY_CREDIT"));
    }

    /**
     * @brief 验证信用附属卡 handler 使用 schema 参数并调用应用层；
     *        Verify supplementary-credit handler uses schema options and invokes application service.
     */
    @Test
    void shouldHandleIssueSupplementaryCreditCardCommand() {
        final IssueSupplementaryCreditCardHandler applicationHandler =
                Mockito.mock(IssueSupplementaryCreditCardHandler.class);
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final PrintStream output = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        final IssueSupplementaryCreditCardCliHandler handler =
                new IssueSupplementaryCreditCardCliHandler(applicationHandler, output);

        when(applicationHandler.handle(any(IssueSupplementaryCreditCardCommand.class))).thenReturn(new IssueCardResult(
                "sup-credit-001",
                "4333****6666",
                CardKind.SUPPLEMENTARY_CREDIT,
                "ACTIVE",
                "cust-004",
                Instant.parse("2026-04-28T11:00:00Z"),
                "credit-primary-001"));

        handler.handle(new ParsedCommand(
                "card issue-supplementary-credit --holder_customer_id cust-004 --primary_credit_card_id credit-primary-001 "
                        + "--credit_card_account_id cc-acc-002 --card_no 4333444455556666",
                List.of("card", "issue-supplementary-credit"),
                Map.of(
                        "holder_customer_id", "cust-004",
                        "primary_credit_card_id", "credit-primary-001",
                        "credit_card_account_id", "cc-acc-002",
                        "card_no", "4333444455556666")));

        verify(applicationHandler, times(1)).handle(any(IssueSupplementaryCreditCardCommand.class));
        final String outputText = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(outputText.contains("SUPPLEMENTARY_CREDIT"));
        assertTrue(outputText.contains("credit-primary-001"));
    }

    /**
     * @brief 验证 `card show` 查询并输出统一卡读模型字段；
     *        Verify `card show` queries and prints unified card read-model fields.
     */
    @Test
    void shouldHandleShowCardCommand() {
        final FindCardHandler applicationHandler = Mockito.mock(FindCardHandler.class);
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final PrintStream output = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        final ShowCardCliHandler handler = new ShowCardCliHandler(applicationHandler, output);

        when(applicationHandler.handle(any(FindCardQuery.class))).thenReturn(new CardResult(
                "sup-credit-001",
                "4333****6666",
                "cust-004",
                "ACTIVE",
                CardKind.SUPPLEMENTARY_CREDIT,
                Instant.parse("2026-04-28T11:00:00Z"),
                null,
                null,
                null,
                "cc-acc-002",
                "credit-primary-001"));

        handler.handle(new ParsedCommand(
                "card show --card_id sup-credit-001",
                List.of("card", "show"),
                Map.of("card_id", "sup-credit-001")));

        verify(applicationHandler, times(1)).handle(any(FindCardQuery.class));
        final String outputText = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(outputText.contains("card_id,masked_card_number,holder_customer_id,card_status,card_kind,issued_at,expired_at,savings_account_id,fx_account_id,credit_card_account_id,primary_card_id"));
        assertTrue(outputText.contains("sup-credit-001,4333****6666,cust-004,ACTIVE,SUPPLEMENTARY_CREDIT,2026-04-28T11:00:00Z,,,"));
        assertTrue(outputText.contains("cc-acc-002,credit-primary-001"));
    }

    /**
     * @brief 验证缺失必填参数时会抛出异常；
     *        Verify missing required options trigger exceptions.
     */
    @Test
    void shouldRejectMissingRequiredOption() {
        final IssueCreditCardHandler applicationHandler = Mockito.mock(IssueCreditCardHandler.class);
        final IssueCreditCardCliHandler handler = new IssueCreditCardCliHandler(applicationHandler, System.out);

        assertThrows(IllegalArgumentException.class, () -> handler.handle(new ParsedCommand(
                "card issue-credit --holder_customer_id cust-003",
                List.of("card", "issue-credit"),
                Map.of("holder_customer_id", "cust-003"))));
    }

    /**
     * @brief 验证 card 命令注册映射；
     *        Verify card command-path registration mappings.
     */
    @Test
    void shouldRegisterCardCommandMappings() {
        final CommandRegistry registry = new CommandRegistry();

        CardCliCommandRegistration.register(registry);

        assertEquals(IssueDebitCardCliHandler.class, registry.findHandlerType("card issue-debit").orElseThrow());
        assertEquals(IssueSupplementaryDebitCardCliHandler.class,
                registry.findHandlerType("card issue-supplementary-debit").orElseThrow());
        assertEquals(IssueCreditCardCliHandler.class, registry.findHandlerType("card issue-credit").orElseThrow());
        assertEquals(IssueSupplementaryCreditCardCliHandler.class,
                registry.findHandlerType("card issue-supplementary-credit").orElseThrow());
        assertEquals(ShowCardCliHandler.class, registry.findHandlerType("card show").orElseThrow());
    }
}
