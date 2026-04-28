package com.moesegfault.banking.domain.shared;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * @brief 日期区间值对象（Date Range Value Object），用于账期与统计周期语义；
 *        Date range value object for billing periods and reporting windows.
 */
public final class DateRange implements Serializable {

    /**
     * @brief 序列化版本号（Serialization Version UID）；
     *        Serialization version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief 区间起始日期（Range Start Date）；
     *        Range start date.
     */
    private final LocalDate start;

    /**
     * @brief 区间结束日期（Range End Date）；
     *        Range end date.
     */
    private final LocalDate end;

    /**
     * @brief 构造日期区间（Construct Date Range）；
     *        Construct a date range.
     *
     * @param start 起始日期（Start date）。
     * @param end   结束日期（End date）。
     */
    private DateRange(final LocalDate start, final LocalDate end) {
        this.start = Objects.requireNonNull(start, "Start date must not be null");
        this.end = Objects.requireNonNull(end, "End date must not be null");
        if (this.end.isBefore(this.start)) {
            throw new BusinessRuleViolation("Date range end must not be before start");
        }
    }

    /**
     * @brief 创建日期区间（Factory Method）；
     *        Create a date range.
     *
     * @param start 起始日期（Start date）。
     * @param end   结束日期（End date）。
     * @return 日期区间对象（Date range object）。
     */
    public static DateRange of(final LocalDate start, final LocalDate end) {
        return new DateRange(start, end);
    }

    /**
     * @brief 返回起始日期（Return Start Date）；
     *        Return start date.
     *
     * @return 起始日期（Start date）。
     */
    public LocalDate start() {
        return start;
    }

    /**
     * @brief 返回结束日期（Return End Date）；
     *        Return end date.
     *
     * @return 结束日期（End date）。
     */
    public LocalDate end() {
        return end;
    }

    /**
     * @brief 判断日期是否在区间内（包含边界）（Contains Date Inclusively）；
     *        Check whether a date is inside the range, inclusive.
     *
     * @param date 待判断日期（Date to test）。
     * @return 在区间内返回 true（true when inside range）。
     */
    public boolean contains(final LocalDate date) {
        Objects.requireNonNull(date, "Date must not be null");
        return !date.isBefore(start) && !date.isAfter(end);
    }

    /**
     * @brief 判断是否与另一区间有交集（Overlap Check）；
     *        Check whether this range overlaps with another range.
     *
     * @param other 另一个日期区间（Another date range）。
     * @return 有交集返回 true（true when overlapping）。
     */
    public boolean overlaps(final DateRange other) {
        Objects.requireNonNull(other, "Other date range must not be null");
        return !this.end.isBefore(other.start) && !other.end.isBefore(this.start);
    }

    /**
     * @brief 返回区间天数（含首尾）（Length in Days Inclusive）；
     *        Return range length in days, inclusive.
     *
     * @return 区间天数（Length in days）。
     */
    public long lengthInDaysInclusive() {
        return ChronoUnit.DAYS.between(start, end) + 1;
    }

    /**
     * @brief 值对象相等判定（Value Object Equality）；
     *        Value-object equality check.
     *
     * @param other 对比对象（Object to compare）。
     * @return 同值返回 true（true when equal）。
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DateRange that)) {
            return false;
        }
        return start.equals(that.start) && end.equals(that.end);
    }

    /**
     * @brief 计算哈希值（Compute Hash Code）；
     *        Compute hash code.
     *
     * @return 哈希值（Hash code）。
     */
    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    /**
     * @brief 返回字符串表示（String Representation）；
     *        Return string representation.
     *
     * @return `start..end` 格式字符串（String in `start..end` format）。
     */
    @Override
    public String toString() {
        return start + ".." + end;
    }
}
