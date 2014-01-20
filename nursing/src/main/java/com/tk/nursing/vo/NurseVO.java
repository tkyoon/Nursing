package com.tk.nursing.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 */
/**
 * <pre>
 * </pre>
 *
 * @title NurseVO
 * @desc
 * @programID NurseVO.java
 * @cdate 2014. 1. 23.
 * @version 1.0
 * @author 윤태경(tkyoon@jcone.co.kr)
 * @see
 *
 */
public class NurseVO {

	/**
	 * 이름
	 */
	private String name = "";

	/**
	 * 나이
	 */
	private int age = 0;

	/**
	 * 직책
	 */
	private String role = "";

	/**
	 * 부서명
	 */
	private String deptNm = "";

	/**
	 * 부서코드
	 */
	private String deptCd = "";

	/**
	 * 우선순위
	 */
	private int priority = 0;

	/**
	 * 메인차지 여부
	 */
	private String mainCharge = "";

	/**
	 * 스트레스 지수
	 */
	private int stressIndices = 0;

	/**
	 * 엑셀에 있는 row number 엑셀에 write하기 위한 값
	 */
	private int excelRowNo = 0;

	/**
	 * off날짜
	 */
	List<Integer> offDate = new ArrayList<Integer>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getDeptNm() {
		return deptNm;
	}

	public void setDeptNm(String deptNm) {
		this.deptNm = deptNm;
	}

	public String getDeptCd() {
		return deptCd;
	}

	public void setDeptCd(String deptCd) {
		this.deptCd = deptCd;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @return String - the mainCharge
	 */
	public String getMainCharge() {
		return mainCharge;
	}

	/**
	 * @param mainCharge the mainCharge to set
	 */
	public void setMainCharge(String mainCharge) {
		this.mainCharge = mainCharge;
	}

	/**
	 * @return List<Integer> - the offDate
	 */
	public List<Integer> getOffDate() {
		return offDate;
	}

	/**
	 * @param offDate the offDate to set
	 */
	public void setOffDate(List<Integer> offDate) {
		this.offDate = offDate;
	}

	public void addOffDate(int date){
		offDate.add(date);
	}

	/**
	 * @return int - the stressIndices
	 */
	public int getStressIndices() {
		return stressIndices;
	}

	/**
	 * @param stressIndices the stressIndices to set
	 */
	public void setStressIndices(int stressIndices) {
		this.stressIndices = stressIndices;
	}

	/**
	 * @return int - the excelRowNo
	 */
	public int getExcelRowNo() {
		return excelRowNo;
	}

	/**
	 * @param excelRowNo the excelRowNo to set
	 */
	public void setExcelRowNo(int excelRowNo) {
		this.excelRowNo = excelRowNo;
	}





}
