package com.moesegfault.banking.presentation.web.card;

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
import com.moesegfault.banking.application.card.result.CardResult;
import com.moesegfault.banking.application.card.result.IssueCardResult;
import com.moesegfault.banking.presentation.web.WebJsonCodec;
import com.moesegfault.banking.presentation.web.WebRequest;
import com.moesegfault.banking.presentation.web.WebResponse;
import java.util.Objects;

/**
 * @brief 卡 REST 控制器（Card REST Controller），负责 card 领域请求解析、应用层调用与 JSON 响应封装；
 *        Card REST controller responsible for card-domain request parsing, application invocation, and JSON response mapping.
 */
public final class CardController {

    /**
     * @brief 主借记卡发卡处理器（Issue Debit Card Handler）；
     *        Application handler for issuing primary debit card.
     */
    private final IssueDebitCardHandler issueDebitCardHandler;

    /**
     * @brief 借记附属卡发卡处理器（Issue Supplementary Debit Card Handler）；
     *        Application handler for issuing supplementary debit card.
     */
    private final IssueSupplementaryDebitCardHandler issueSupplementaryDebitCardHandler;

    /**
     * @brief 主信用卡发卡处理器（Issue Credit Card Handler）；
     *        Application handler for issuing primary credit card.
     */
    private final IssueCreditCardHandler issueCreditCardHandler;

    /**
     * @brief 信用附属卡发卡处理器（Issue Supplementary Credit Card Handler）；
     *        Application handler for issuing supplementary credit card.
     */
    private final IssueSupplementaryCreditCardHandler issueSupplementaryCreditCardHandler;

    /**
     * @brief 卡查询处理器（Find Card Handler）；
     *        Application handler for card-detail query.
     */
    private final FindCardHandler findCardHandler;

    /**
     * @brief JSON 编解码器（JSON Codec）；
     *        JSON codec for request/response serialization.
     */
    private final WebJsonCodec webJsonCodec;

    /**
     * @brief 构造卡 REST 控制器（Construct Card REST Controller）；
     *        Construct card REST controller with required handlers.
     *
     * @param issueDebitCardHandler 主借记卡发卡处理器（Issue debit-card handler）。
     * @param issueSupplementaryDebitCardHandler 借记附属卡发卡处理器（Issue supplementary debit-card handler）。
     * @param issueCreditCardHandler 主信用卡发卡处理器（Issue credit-card handler）。
     * @param issueSupplementaryCreditCardHandler 信用附属卡发卡处理器（Issue supplementary credit-card handler）。
     * @param findCardHandler 卡查询处理器（Find-card handler）。
     * @param webJsonCodec JSON 编解码器（JSON codec）。
     */
    public CardController(
            final IssueDebitCardHandler issueDebitCardHandler,
            final IssueSupplementaryDebitCardHandler issueSupplementaryDebitCardHandler,
            final IssueCreditCardHandler issueCreditCardHandler,
            final IssueSupplementaryCreditCardHandler issueSupplementaryCreditCardHandler,
            final FindCardHandler findCardHandler,
            final WebJsonCodec webJsonCodec
    ) {
        this.issueDebitCardHandler = Objects.requireNonNull(issueDebitCardHandler, "issueDebitCardHandler must not be null");
        this.issueSupplementaryDebitCardHandler = Objects.requireNonNull(
                issueSupplementaryDebitCardHandler,
                "issueSupplementaryDebitCardHandler must not be null");
        this.issueCreditCardHandler = Objects.requireNonNull(issueCreditCardHandler, "issueCreditCardHandler must not be null");
        this.issueSupplementaryCreditCardHandler = Objects.requireNonNull(
                issueSupplementaryCreditCardHandler,
                "issueSupplementaryCreditCardHandler must not be null");
        this.findCardHandler = Objects.requireNonNull(findCardHandler, "findCardHandler must not be null");
        this.webJsonCodec = Objects.requireNonNull(webJsonCodec, "webJsonCodec must not be null");
    }

    /**
     * @brief 处理主借记卡发卡请求（Handle Issue Debit Card Request）；
     *        Handle REST request for issuing primary debit card.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse issueDebitCard(final WebRequest request) {
        final IssueDebitCardRequestDto requestDto = readBody(request, IssueDebitCardRequestDto.class);
        final IssueCardResult issueResult = issueDebitCardHandler.handle(new IssueDebitCardCommand(
                requestDto.holder_customer_id(),
                requestDto.savings_account_id(),
                requestDto.fx_account_id(),
                requestDto.card_no()));
        return createdResponse(issueResult);
    }

    /**
     * @brief 处理借记附属卡发卡请求（Handle Issue Supplementary Debit Card Request）；
     *        Handle REST request for issuing supplementary debit card.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse issueSupplementaryDebitCard(final WebRequest request) {
        final IssueSupplementaryDebitCardRequestDto requestDto = readBody(
                request,
                IssueSupplementaryDebitCardRequestDto.class);
        final IssueCardResult issueResult = issueSupplementaryDebitCardHandler.handle(new IssueSupplementaryDebitCardCommand(
                requestDto.holder_customer_id(),
                requestDto.primary_debit_card_id(),
                requestDto.card_no()));
        return createdResponse(issueResult);
    }

    /**
     * @brief 处理主信用卡发卡请求（Handle Issue Credit Card Request）；
     *        Handle REST request for issuing primary credit card.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse issueCreditCard(final WebRequest request) {
        final IssueCreditCardRequestDto requestDto = readBody(request, IssueCreditCardRequestDto.class);
        final IssueCardResult issueResult = issueCreditCardHandler.handle(new IssueCreditCardCommand(
                requestDto.holder_customer_id(),
                requestDto.credit_card_account_id(),
                requestDto.card_no()));
        return createdResponse(issueResult);
    }

    /**
     * @brief 处理信用附属卡发卡请求（Handle Issue Supplementary Credit Card Request）；
     *        Handle REST request for issuing supplementary credit card.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse issueSupplementaryCreditCard(final WebRequest request) {
        final IssueSupplementaryCreditCardRequestDto requestDto = readBody(
                request,
                IssueSupplementaryCreditCardRequestDto.class);
        final IssueCardResult issueResult = issueSupplementaryCreditCardHandler.handle(new IssueSupplementaryCreditCardCommand(
                requestDto.holder_customer_id(),
                requestDto.primary_credit_card_id(),
                requestDto.credit_card_account_id(),
                requestDto.card_no()));
        return createdResponse(issueResult);
    }

    /**
     * @brief 处理卡详情查询请求（Handle Show Card Request）；
     *        Handle REST request for card-detail query.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse showCard(final WebRequest request) {
        final WebRequest normalizedRequest = Objects.requireNonNull(request, "request must not be null");
        final String cardId = normalizedRequest.pathParam(CardWebSchema.PATH_PARAM_CARD_ID)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Missing required path parameter: " + CardWebSchema.FIELD_CARD_ID));

        final CardResult cardResult = findCardHandler.handle(new FindCardQuery(cardId));
        final CardResponseDto responseDto = CardResponseDto.fromCardResult(cardResult);
        return webJsonCodec.toJsonResponse(200, responseDto);
    }

    /**
     * @brief 读取 JSON 请求体（Read JSON Request Body）；
     *        Read JSON request body into target DTO.
     *
     * @param request Web 请求（Web request）。
     * @param targetType 目标 DTO 类型（Target DTO type）。
     * @param <T> DTO 类型参数（DTO type parameter）。
     * @return DTO 对象（DTO instance）。
     */
    private <T> T readBody(final WebRequest request, final Class<T> targetType) {
        final WebRequest normalizedRequest = Objects.requireNonNull(request, "request must not be null");
        final Class<T> normalizedType = Objects.requireNonNull(targetType, "targetType must not be null");
        return webJsonCodec.deserialize(normalizedRequest.bodyText(), normalizedType);
    }

    /**
     * @brief 构建 201 创建响应（Build HTTP 201 Created Response）；
     *        Build HTTP 201 response for card-issuance success.
     *
     * @param issueResult 发卡结果（Issue result）。
     * @return Web 响应（Web response）。
     */
    private WebResponse createdResponse(final IssueCardResult issueResult) {
        final CardResponseDto responseDto = CardResponseDto.fromIssueResult(issueResult);
        return webJsonCodec.toJsonResponse(201, responseDto)
                .withHeader("Location", CardWebSchema.PATH_CARDS + "/" + responseDto.card_id());
    }
}

