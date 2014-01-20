package com.tk.nursing.vo;

import java.util.ArrayList;
import java.util.List;

public class MonthVO {

	/**
	 * 지난 달 마지막날짜 휴일여부
	 */
	private boolean isHolidayLastMonthLastDay = false;

	/**
	 * 이번달의 마지막 날짜
	 */
	private int lastDay = 0;

	/**
	 * 쉬는 날짜들
	 */
	private List<String> holidayList = new ArrayList<String>();

	/**
	 * @return boolean - the isHolidayLastMonthLastDay
	 */
	public boolean isHolidayLastMonthLastDay() {
		return isHolidayLastMonthLastDay;
	}

	/**
	 * @param isHolidayLastMonthLastDay the isHolidayLastMonthLastDay to set
	 */
	public void setHolidayLastMonthLastDay(boolean isHolidayLastMonthLastDay) {
		this.isHolidayLastMonthLastDay = isHolidayLastMonthLastDay;
	}

	/**
	 * @return int - the lastDay
	 */
	public int getLastDay() {
		return lastDay;
	}

	/**
	 * @param lastDay the lastDay to set
	 */
	public void setLastDay(int lastDay) {
		this.lastDay = lastDay;
	}

	/**
	 * @return List - the holidayList
	 */
	public List getHolidayList() {
		return holidayList;
	}

	/**
	 * @param holidayList the holidayList to set
	 */
	public void setHolidayList(List holidayList) {
		this.holidayList = holidayList;
	}

	public void addHoliday(String date){
		holidayList.add(date);
	}




}
