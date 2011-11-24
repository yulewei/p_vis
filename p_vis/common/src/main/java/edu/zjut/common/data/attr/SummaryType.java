package edu.zjut.common.data.attr;

public enum SummaryType {
	NULL,

	/**
	 * 求和
	 */
	SUM,

	/**
	 * 均值
	 */
	MEAN,

	/**
	 * 计数
	 */
	COUNT,

	/**
	 * 计数, Unique Count
	 */
	UNI_COUNT,

	/**
	 * 最小值
	 */
	MIN,

	/**
	 * 最大值
	 */
	MAX,

	/**
	 * 标准差, Standard Deviation
	 */
	STD_DEV,

	/**
	 * 变异系数, coefficient of variation
	 */
	CV
}
