package com.tk.nursing.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tk.nursing.common.Common;
import com.tk.nursing.common.CommonUtils;
import com.tk.nursing.common.Constants;
import com.tk.nursing.vo.MonthVO;
import com.tk.nursing.vo.NurseVO;

public class ExcelImporter {

	// COL CODE
	// 파일
	public final static int FILE_IDX_A_0_YUNBUN = 0;
	public final static int FILE_IDX_B_1_RS_TYPE_CD = 1;
	public final static int FILE_IDX_C_2_TITLE = 2;
	public final static int FILE_IDX_D_3_CONTENT = 3;
	public final static int FILE_IDX_E_4_PUB_DIV_CD = 4;
	public final static int FILE_IDX_F_5_PART_PUB_GRD_CD = 5;
	public final static int FILE_IDX_G_6_PRI_PAGE = 6;
	public final static int FILE_IDX_H_7_PRDCT_YYMMDD = 7;
	public final static int FILE_IDX_I_8_PRSVN_PRID_CD = 8;
	public final static int FILE_IDX_J_9_ELCT_YN = 9;
	public final static int FILE_IDX_K_10_REL_DOC_NO = 10;
	public final static int FILE_IDX_L_11_REMARK = 11;
	public final static int FILE_IDX_M_12_QTY = 12;
	public final static int FILE_IDX_N_13_PRDCT_ORG = 13;

	// 이하 유형별 값
	public final static int FILE_IDX_O_14 = 14;
	public final static int FILE_IDX_P_15 = 15;
	public final static int FILE_IDX_Q_16 = 16;
	public final static int FILE_IDX_R_17 = 17;
	// 연번색에따른 유성구 자료
	public final static int FILE_IDX_S_18 = 18;

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	public final int DATA = -1;
	public final static int FIRST_IDX = 1;
	// RGB 색 주황색(엑셀 import시에 주황색은 유성구 자료)
	private final static String HSSF_RGB_ORANGE = "FFFF:CCCC:0";
	private final static String XSSF_RGB_ORANGE = "FFFFC"; // FFFFCC00 -> FFFFC
	boolean isHSSF;
	String excelData[][];
	FileOutputStream fileOut;

	String outPath = "";

	public ExcelImporter(String filePath, String fileName) throws Exception {
		String fileFullPath = filePath + fileName;

		//output xls 파일 복사
		File file = new File(fileFullPath);
		String outputFileFullPath = filePath + FileUtils.removeExtension(fileName)+ "_output." + FileUtils.getExtension(fileName);
		outPath = outputFileFullPath;
		File fileResult = new File(outputFileFullPath);
		if(fileResult.exists()){
			fileResult.delete();
		}

		FileUtils.copyFile(file, fileResult);

		InputStream inp = null;

		try {
			inp = new FileInputStream(fileResult);

			// .xls .xlsx구분
			this.setIsHSSF(fileName);

			String data[][] = this.getDataByArray(inp, 2, 0); // row = 2 => 상단
			// 타이틀 제외
			inp.close();
			this.setExcelDataArray(data);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != inp) {
				inp.close();
			}
		}
	}

	public void setIsHSSF(String fileName) {
		if ('s' == fileName.charAt(fileName.length() - 1)) {
			this.isHSSF = true;
		} else if ('x' == fileName.charAt(fileName.length() - 1)) {
			this.isHSSF = false;
		}
	}

	/**
	 * <pre>
	 * 엑셀의 상단 타이틀 읽는 부분
	 * startRowNum : 행 시작위치
	 * startColNum : 열 시작위치
	 * rowsCnt     : 행의 갯수
	 * </pre>
	 *
	 * @param startRowNum
	 * @param startColNum
	 * @param rowsCnt
	 * @param colMaxCnt
	 * @return
	 * @throws Exception
	 */
	public String[][] getTopDataByArray(InputStream inp, int startRowNum, int startColNum, int rowsCnt) throws Exception {
		return this.excelReadPoi(inp, startRowNum, startColNum, rowsCnt);
	}

	/**
	 * <pre>
	 * 엑셀의 데이터 읽는 부분
	 * startRowNum : 행 시작위치
	 * startColNum : 열 시작위치
	 *
	 * </pre>
	 *
	 * @param startRowNum
	 * @param startColNum
	 * @param colMaxCnt
	 * @return
	 * @throws Exception
	 */
	public String[][] getDataByArray(InputStream inp, int startRowNum, int startColNum) throws Exception {
		return this.excelReadPoi(inp, startRowNum, startColNum, this.DATA);
	}

	public String[][] excelReadPoi(InputStream inp, int startRowNum, int startColNum, int rowsCnt) throws Exception {
		String[][] data = null;
		Workbook workbook = null;
		Sheet sheet = null;

		Row ds = null;
		Row offDs = null;

		Cell cell = null;
		Cell offCell = null;

		int rowCnt = 0;
		int colCnt = 0;

		List<NurseVO> nurseList = new ArrayList<NurseVO>();
		List<NurseVO> nurseAChargeGrp = new ArrayList<NurseVO>();
		List<NurseVO> nurseADutyGrp = new ArrayList<NurseVO>();
		List<NurseVO> nurseBChargeGrp = new ArrayList<NurseVO>();
		List<NurseVO> nurseBDutyGrp = new ArrayList<NurseVO>();
		MonthVO mVo = new MonthVO();

		try {
			workbook = WorkbookFactory.create(inp);
			sheet = workbook.getSheetAt(0);

			rowCnt = sheet.getLastRowNum();
			colCnt = sheet.getRow(1).getLastCellNum() + 1;

			//log.debug("last row no=" + rowCnt);
			//log.debug("last col no=" + colCnt);

			data = new String[rowCnt][colCnt];

			//Month에 대한 정보를 생성한다. F열부터 시작(5)
			Row dateRow = sheet.getRow(Constants.DATE_STRT_ROW);
			for (int i = Constants.DATE_STRT_COL; i < colCnt; i++) {
				cell = dateRow.getCell(i);
				if(cell != null){
					//cell type이 숫자일 경우, 날짜일 경우, 마지막날짜를 구한다.
					if(HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType()){
						mVo.setLastDay((int)cell.getNumericCellValue());
						CellStyle cStyle = cell.getCellStyle();
						HSSFColor hcolor = (HSSFColor) cStyle.getFillForegroundColorColor();
						if(Constants.HOLIDAY_HEX_COLOR.equals(hcolor.getHexString())){
							mVo.addHoliday(String.valueOf((int)cell.getNumericCellValue()));
						}
					}
				}
			} // End i for

			log.debug("이번달의 마지막일자 = {}", mVo.getLastDay());
			log.debug("이번달의 휴일 = {}", mVo.getHolidayList());

			int tempOffDate = 0;
			String tempOffDay = StringUtils.EMPTY;

			for (int i = Constants.STRT_ROW_IDX; i < rowCnt; i++) {
				ds = sheet.getRow(i);
				NurseVO nurseVO = new NurseVO();
				nurseVO.setExcelRowNo(i);

				for (int j = 0; j < colCnt; j++) {
					cell = ds.getCell(j);


					if(j == Constants.DEPT_COL_IDX){
						//부서명 설정
						nurseVO.setDeptNm(cell.getStringCellValue());

					}else if(j == Constants.ROLE_COL_IDX){
						//직책 설정
						nurseVO.setRole(cell.getStringCellValue());

					}else if(j == Constants.NAME_COL_IDX){
						//이름 설정
						nurseVO.setName(cell.getStringCellValue());

						//Main Charge를 볼수 있나 여부 체크
						if(i<Constants.MAIN_CHARGE_STRD){
							nurseVO.setMainCharge("true");
							log.debug("메인차지 = {}", nurseVO.getName());

						}else{
							nurseVO.setMainCharge("false");
							log.debug("메인차지 X= {}", nurseVO.getName());

						}

					}

					//off일 설정
					if(cell != null && HSSFCell.CELL_TYPE_STRING == cell.getCellType()){
						if(Constants.OFF_NM.equalsIgnoreCase(cell.getStringCellValue())){
							offDs = sheet.getRow(Constants.DATE_STRT_ROW);
							offCell = offDs.getCell(j);
							tempOffDate = (int)offCell.getNumericCellValue();

							offDs = sheet.getRow(Constants.DATE_STRT_ROW+1);
							offCell = offDs.getCell(j);
							tempOffDay = offCell.getStringCellValue();

							log.debug("@@ {} {}일 {}요일 {}", nurseVO.getName(), tempOffDate, tempOffDay, cell.getStringCellValue());
							nurseVO.addOffDate(tempOffDate);
						}
					}

				} // End j for

				if(nurseVO.getDeptNm().equals(Constants.DEPT_A_NM)){
					//nurseAGrp.add(nurseVO);
					if(Boolean.parseBoolean(nurseVO.getMainCharge())){
						nurseAChargeGrp.add(nurseVO);

					}else{
						nurseADutyGrp.add(nurseVO);
					}

				}else if(nurseVO.getDeptNm().equals(Constants.DEPT_B_NM)){
					nurseBChargeGrp.add(nurseVO);

				}

				nurseList.add(nurseVO);

			} //End i for

			log.debug("@@ nurse A Charge Grp.sz = {}", nurseAChargeGrp.size());
			log.debug("@@ nurse A Duty Grp.sz = {}", nurseADutyGrp.size());
//			log.debug("@@ nurseList = {}", nurseList);


			/*
			for (int i = 0; i < nurseList.size(); i++) {
				log.debug(nurseList.get(i).getName());

			}*/

			//날짜
			boolean tmp = false;//이전일이 휴일인지 여부
			boolean isHolidayToday = false;


			for (int i = 1; i <= mVo.getLastDay(); i++) {
				log.debug("시작일 = {}", i);
				if(mVo.getHolidayList().contains(String.valueOf(i))){
					isHolidayToday = true;
				}

				this.pickNightMember(nurseAChargeGrp, i);
				this.pickNightMember(nurseADutyGrp, i);


				if(isHolidayToday){
					//휴일일 경우


				}else{
					//평일일 경우

				}


			}


		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return data;
	}



	/**
	 * <pre>
	 * Night멤버를 찾아라
	 * 1.Charge 1명, Duty 1명
	 * </pre>
	 *
	 * @param nurseGrp
	 */
	public void pickNightMember(List<NurseVO> nurseGrp, int today) {
		NurseVO vo = new NurseVO();
		int ran = 0;

		boolean loop = true;
		while(loop){
			log.debug("@@ loop");
			ran = Common.getRandomInt(nurseGrp.size());
			vo = nurseGrp.get(ran);

			//off인지 체크하여 off에는 할당하지 않는다.
			if(!vo.getOffDate().contains(today)){
				loop = false;
			}
		}

		cellWrite("", vo.getExcelRowNo(), today+4, "N");

		String str = CommonUtils.voToJsonString(vo);
		log.debug(str);

	}


	public void cellWrite(String path, int row, int col, String msg) {

		Workbook workbook = null;
		FileInputStream fis = null;
		Sheet sheet = null;
		Cell cell = null;

		try {

			fis = new FileInputStream(outPath);
			workbook = WorkbookFactory.create(fis);
			sheet = workbook.getSheetAt(0);

			Row eRow = sheet.getRow(row);

			// 등록여부 메세지 표시
			cell = eRow.createCell(col);
			cell.setCellValue(msg);

			// 기본 셀스타일
			//CellStyle cellStyle2 = workbook.createCellStyle();
			//cellStyle2.cloneStyleFrom(eRow.getCell(4).getCellStyle());

//			// 에러 스타일
//			if (!isDone) {
//				cellStyle2.setFillPattern(CellStyle.SOLID_FOREGROUND);
//				cellStyle2.setFillForegroundColor(HSSFColor.YELLOW.index);
//			}
//			cell.setCellStyle(cellStyle2);
			this.closeWorkbook(workbook, outPath);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


















































 	public String[][] excelReadPoi_org(InputStream inp, int startRowNum, int startColNum, int rowsCnt) throws Exception {
		Workbook workbook = null;
		Sheet sheet = null;
		Cell cell = null;
		CellStyle cellStyle = null;
		String[][] data = null;

		try {
			workbook = WorkbookFactory.create(inp);
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			sheet = workbook.getSheetAt(0);

			// 최대row 길이(헤더일경우)
			int rowMaxCnt = 0;
			// rowsCnt = 0 데이터일경우 마지막까지 길이 구함
			if (this.DATA == rowsCnt) {
				rowMaxCnt = sheet.getLastRowNum() + 1;
				// 배열row길이
				rowsCnt = rowMaxCnt - startRowNum;
			} else {
				rowMaxCnt = startRowNum + rowsCnt;
			}

			int firstColMaxNum = sheet.getRow(startRowNum).getLastCellNum() + 1;

			data = new String[rowsCnt][firstColMaxNum];
			// idx : 엑셀 row시작위치, arrayIdx : 배열 시작위치
			for (int idx = startRowNum, arrayIdx = 0; idx < rowMaxCnt; idx++, arrayIdx++) {
				int cellsCnt = 0;
				if (null != sheet.getRow(idx)) {
					cellsCnt = sheet.getRow(idx).getLastCellNum(); // cellNum
				}
				// col
				Row ds = sheet.getRow(idx);
				for (int i = startColNum; i < cellsCnt; i++) {

					cell = ds.getCell(i);
					// 1번위치 연번의 배경스타일이 들어감(다음루프 값 null이므로 통과)
					if (0 == i) {
						cellStyle = cell.getCellStyle();

						// 유성구기록물여부 (YN) => 배열 제일 마지막에 등록
						if (this.isHSSF) {// .xls
							HSSFColor hcolor = (HSSFColor) cellStyle.getFillForegroundColorColor();

							// 연번데이터 주황색일때만 유성구기록물
							if (hcolor.getHexString().equals(ExcelImporter.HSSF_RGB_ORANGE)) {
								// 2(유성구기록물 여부)
								data[arrayIdx][firstColMaxNum - 1] = "Y";
							} else {
								data[arrayIdx][firstColMaxNum - 1] = "N";
							}
						} else {// .xlsx
							String rGBHex = "";
							XSSFColor xcolor = (XSSFColor) cellStyle.getFillForegroundColorColor();
							try {
								rGBHex = xcolor.getARGBHex();
								rGBHex = rGBHex.substring(0, 5);
							} catch (Exception e) {
								// 채우기없음 시 상단 코드에서 Exception발생
							}

							if (rGBHex.equals(ExcelImporter.XSSF_RGB_ORANGE)) {
								// 2(유성구기록물 여부)
								data[arrayIdx][firstColMaxNum - 1] = "Y";
							} else {
								data[arrayIdx][firstColMaxNum - 1] = "N";
							}
						}

					}

					// cell이 널이아닌경우 데이터 삽입
					if (null != cell) {
//						switch (cell.getCellType()) {
//						case 0: // Cell.CELL_TYPE_NUMERIC :
//
//							if (ExcelImporter.FILE_IDX_B_1_RS_TYPE_CD == i || ExcelImporter.FILE_IDX_E_4_PUB_DIV_CD == i || ExcelImporter.FILE_IDX_I_8_PRSVN_PRID_CD == i) {// code
//								data[arrayIdx][i] = this.dataToCode(cell.toString());
//							} else if (ExcelImporter.FILE_IDX_A_0_YUNBUN == i || ExcelImporter.FILE_IDX_M_12_QTY == i) {// number
//								String intStr = cell.toString();
//								data[arrayIdx][i] = intStr.substring(0, intStr.length() - 2);// .0
//								// 소수점제거
//							} else if (ExcelImporter.FILE_IDX_H_7_PRDCT_YYMMDD == i) {
//								// 마지막이 .0이면 제거
//								data[arrayIdx][i] = this.removeRoundZero(cell.toString());
//							} else {
//								// 자료유형
//								if (data[arrayIdx][ExcelImporter.FILE_IDX_B_1_RS_TYPE_CD].equals(UacConstants._RS_TYPE_CD_AV)) { // 시청각기록물
//									// 코드
//									if (ExcelImporter.FILE_IDX_O_14 == i || ExcelImporter.FILE_IDX_P_15 == i) {
//										data[arrayIdx][i] = this.dataToCode(cell.toString());
//									} else {
//										data[arrayIdx][i] = cell.toString();
//									}
//								} else if (data[arrayIdx][ExcelImporter.FILE_IDX_B_1_RS_TYPE_CD].equals(UacConstants._RS_TYPE_CD_MUS)) { // 행정박물
//									// 코드
//									if (ExcelImporter.FILE_IDX_O_14 == i || ExcelImporter.FILE_IDX_P_15 == i || ExcelImporter.FILE_IDX_Q_16 == i || ExcelImporter.FILE_IDX_R_17 == i) {
//										data[arrayIdx][i] = this.dataToCode(cell.toString());
//									} else if (ExcelImporter.FILE_IDX_S_18 == i) {
//										if (data[arrayIdx][ExcelImporter.FILE_IDX_R_17].equals(UacConstants._MUS_SIZE_TYPE_CD_SIZE_INPUT)) {
//											data[arrayIdx][i] = this.dataToCode(cell.toString());
//										} else {
//											data[arrayIdx][i] = cell.toString();
//										}
//									} else {
//										data[arrayIdx][i] = cell.toString();
//									}
//								} else if (data[arrayIdx][ExcelImporter.FILE_IDX_B_1_RS_TYPE_CD].equals(UacConstants._RS_TYPE_CD_ISS)) { // 간행물
//									// 코드
//									if (ExcelImporter.FILE_IDX_P_15 == i) {
//										data[arrayIdx][i] = this.dataToCode(cell.toString());
//									} else {
//										data[arrayIdx][i] = cell.toString();
//									}
//								} else {
//									data[arrayIdx][i] = cell.toString();
//								}
//							}
//							break;
//						case 1: // Cell.CELL_TYPE_STRING :
//
//							if (ExcelImporter.FILE_IDX_I_8_PRSVN_PRID_CD == i) {
//								data[arrayIdx][i] = this.removeRoundZero(cell.toString());
//							} else {
//								data[arrayIdx][i] = cell.getRichStringCellValue().getString();
//							}
//							break;
//						case Cell.CELL_TYPE_BOOLEAN:
//							data[arrayIdx][i] = cell.getBooleanCellValue() + "";
//							break;
//						case Cell.CELL_TYPE_FORMULA:
//							if (evaluator.evaluateFormulaCell(cell) == Cell.CELL_TYPE_NUMERIC) {
//								if (DateUtil.isCellDateFormatted(cell)) {
//									data[arrayIdx][i] = "";
//								} else {
//									Double value = new Double(cell.getNumericCellValue());
//									if (value.longValue() == value.doubleValue()) {
//										data[arrayIdx][i] = data[idx][i] = Long.toString(value.longValue());
//									} else {
//										data[arrayIdx][i] = data[idx][i] = value.toString();
//									}
//								}
//							} else if (evaluator.evaluateFormulaCell(cell) == Cell.CELL_TYPE_STRING) {
//								data[arrayIdx][i] = cell.getStringCellValue();
//							} else if (evaluator.evaluateFormulaCell(cell) == Cell.CELL_TYPE_BOOLEAN) {
//								data[arrayIdx][i] = new Boolean(cell.getBooleanCellValue()).toString();
//							} else {
//								data[arrayIdx][i] = cell.toString();
//							}
//							break;
//						default:
//						}
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * <pre>
	 * arrayToRgFileVo
	 * </pre>
	 *
	 * @Method Name : arrayToRgFileVo
	 * @param data
	 * @param row
	 * @return
	 */
//	public RgFileVO arrayToRgFileVo(String[][] data, int row) throws Exception {
//		RgFileVO vo = new RgFileVO();
//		vo.setFolderNm(data[row][ExcelImporter.FILE_IDX_A_0_YUNBUN]);
//		vo.setRsTypeCd(data[row][ExcelImporter.FILE_IDX_B_1_RS_TYPE_CD]);
//		vo.setFileTitle(data[row][ExcelImporter.FILE_IDX_C_2_TITLE]);
//		vo.setFileContents(data[row][ExcelImporter.FILE_IDX_D_3_CONTENT]);
//		vo.setPubDivCd(data[row][ExcelImporter.FILE_IDX_E_4_PUB_DIV_CD]);
//		vo.setPartPubGrdCd(data[row][ExcelImporter.FILE_IDX_F_5_PART_PUB_GRD_CD]);
//		vo.setPriPage(data[row][ExcelImporter.FILE_IDX_G_6_PRI_PAGE]);
//		vo.setPrdctYymmdd(this.getPrdctYymmddFrom(data[row][ExcelImporter.FILE_IDX_H_7_PRDCT_YYMMDD]));
//		// 보존기간
//		vo.setPrsvnPridCd(data[row][ExcelImporter.FILE_IDX_I_8_PRSVN_PRID_CD]);
//		vo.setElctYn(data[row][ExcelImporter.FILE_IDX_J_9_ELCT_YN]);
//		vo.setRelDocNo(data[row][ExcelImporter.FILE_IDX_K_10_REL_DOC_NO]);
//		vo.setRemark(data[row][ExcelImporter.FILE_IDX_L_11_REMARK]);
//		vo.setQty(data[row][ExcelImporter.FILE_IDX_M_12_QTY]);
//
//		vo.setPrdctOrg(data[row][ExcelImporter.FILE_IDX_N_13_PRDCT_ORG]);
//		vo.setOrgFileYn(data[row][data[row].length - 1]);
//
//		return vo;
//	}

	/**
	 * <pre>
	 * vo - 간행물
	 * </pre>
	 *
	 * @Method Name : arrayToFlIssueVO
	 * @param data
	 * @param row
	 * @return
	 */
//	public FlIssueVO arrayToFlIssueVO(String[][] data, int row) {
//		FlIssueVO vo = new FlIssueVO();
//		vo.setPrdctDept(data[row][ExcelImporter.FILE_IDX_O_14]);
//		vo.setIsuOrgDivCd(data[row][ExcelImporter.FILE_IDX_P_15]);
//		vo.setSubmitYn(data[row][ExcelImporter.FILE_IDX_Q_16]);
//		vo.setRegNo(data[row][ExcelImporter.FILE_IDX_R_17]);
//
//		return vo;
//	}

	/**
	 * <pre>
	 * vo - 시청각기록물
	 * </pre>
	 *
	 * @Method Name : arrayToFlAudiovisualVO
	 * @param data
	 * @param row
	 * @return
	 */
//	public FlAudiovisualVO arrayToFlAudiovisualVO(String[][] data, int row) {
//		FlAudiovisualVO vo = new FlAudiovisualVO();
//		vo.setAvTypeCd(data[row][ExcelImporter.FILE_IDX_O_14]);
//		vo.setOrgcpyMdaCd(data[row][ExcelImporter.FILE_IDX_P_15]);
//		vo.setPrdctPlace(data[row][ExcelImporter.FILE_IDX_Q_16]);
//		vo.setCharc(data[row][ExcelImporter.FILE_IDX_R_17]);
//
//		return vo;
//	}
//
//	/**
//	 * <pre>
//	 * vo - 행정박물
//	 * </pre>
//	 *
//	 * @Method Name : arrayToFlMuseumVO
//	 * @param data
//	 * @param row
//	 * @return
//	 */
//	public FlMuseumVO arrayToFlMuseumVO(String[][] data, int row) {
//		FlMuseumVO vo = new FlMuseumVO();
//		vo.setMusTypeCd(this.museEtcCd(data[row][ExcelImporter.FILE_IDX_O_14]));
//		vo.setTypeEtc(this.museEtcStr(data[row][ExcelImporter.FILE_IDX_O_14]));
//		vo.setMusFrmCd(this.museEtcCd(data[row][ExcelImporter.FILE_IDX_P_15]));
//		vo.setFrmEtc(this.museEtcStr(data[row][ExcelImporter.FILE_IDX_P_15]));
//		vo.setMusMatCd(this.museEtcCd(data[row][ExcelImporter.FILE_IDX_Q_16]));
//		vo.setMatEtc(this.museEtcStr(data[row][ExcelImporter.FILE_IDX_Q_16]));
//		vo.setMusSizeTypeCd(this.museEtcCd(data[row][ExcelImporter.FILE_IDX_R_17]));
//		vo.setMusSize(data[row][ExcelImporter.FILE_IDX_S_18]);
//
//		return vo;
//	}

	public String museEtcCd(String museEtcCd){
		if(museEtcCd.length()==2){
			return museEtcCd;
		}else{
			return museEtcCd.substring(0,2);
		}
	}

//	public String museEtcStr(String museEtcCd){
//		String str[] = museEtcCd.split(UacConstants.ETC_REGEX);
//		if(str.length>1){
//			return str[1];
//		}
//		return null;
//	}

	/**
	 * <pre>
	 * 엑셀데이터를 DB저장 폼에 알맞은 생산연도 폼 리턴
	 * ex)
	 * val	   		return
	 * 2001			2001-01-01
	 * 2001.03  	2001-03-01
	 * 2001.03.03	2001-03-03
	 * null			9999-12-31
	 * </pre>
	 *
	 * @Method Name : getPrdctYymmddFrom
	 * @param prdctYymmdd
	 * @return
	 */
	public String getPrdctYymmddFrom(String prdctYymmdd) throws Exception {
		String rstStr = null;

		try {
			if (!StringUtils.isEmpty(prdctYymmdd)) {
//				String[] prdctStr = prdctYymmdd.split(UacConstants.EXCEL_REGEX);
//				switch (prdctStr.length) {
//				case 1:
//					rstStr = prdctStr[0] + UacConstants.DATA_REGEX + "01" + UacConstants.DATA_REGEX + "01";
//
//					break;
//				case 2:
//					if (Integer.parseInt(prdctStr[1]) < 10 && prdctStr[1].length() < 2) {
//						rstStr = prdctStr[0] + UacConstants.DATA_REGEX + "0" + prdctStr[1];
//					} else {
//						rstStr = prdctStr[0] + UacConstants.DATA_REGEX + prdctStr[1];
//					}
//					rstStr = rstStr + UacConstants.DATA_REGEX + "01";
//					break;
//				case 3:
//					// 2001.03
//					if (Integer.parseInt(prdctStr[1]) < 10 && prdctStr[1].length() < 2) {
//						rstStr = prdctStr[0] + UacConstants.DATA_REGEX + "0" + prdctStr[1];
//					} else {
//						rstStr = prdctStr[0] + UacConstants.DATA_REGEX + prdctStr[1];
//					}
//
//					// 2001.03.03
//					if (Integer.parseInt(prdctStr[2]) < 10 && prdctStr[2].length() < 2) {
//						rstStr = rstStr + UacConstants.DATA_REGEX + "0" + prdctStr[2];
//					} else {
//						rstStr = rstStr + UacConstants.DATA_REGEX + prdctStr[2];
//					}
//					break;
//
//				default:
//					break;
//				}
			} else {
				// null -> 9999-12-31
//				rstStr = "9999" + UacConstants.DATA_REGEX + "12" + UacConstants.DATA_REGEX + "31";
			}
		} catch (Exception e) {
			this.log.error(e.getMessage());
			throw e;
		}

		return rstStr;
	}

	public String[][] getExcelDataArray() {
		return this.excelData;
	}

	public void setExcelDataArray(String[][] data) {
		this.excelData = data;
	}

	public String getRsTypeCdFromExcelData() {
		return this.excelData[2][ExcelImporter.FILE_IDX_B_1_RS_TYPE_CD];
	}

	/**
	 * <pre>
	 * 엑셀 데이터 Row Count 반환
	 * 엑셀 Row 공백이나 스타일적용시 데이터로 인식하므로, 모든 열이 공백이면
	 * 그이상의 로우값만 데이터로 인정
	 * </pre>
	 *
	 * @Method Name : getExcelDataRowLength
	 * @return
	 */
	public int getExcelDataRowLength() {
		int rowCnt = 0;
		boolean isAllColNull = false;
		for (int i = 0; i < this.excelData.length; i++) {
			// 자료유형, 제목,생산년도, 보존기간 null
			if (this.excelData[i][2] == null && this.excelData[i][2] == null && this.excelData[i][5] == null && this.excelData[i][8] == null) {
				// 모든 값 null일때
				for (int col = 0; col < this.excelData[i].length; col++) {
					if (null == this.excelData[i][col]) {
						isAllColNull = true;
					}
				}
				if (isAllColNull) {
					return rowCnt;
				}
			}
			rowCnt++;
		}
		return rowCnt;
	}

	private String dataToCode(String data) {
		data = data.replace(".0", "");
		if (data.length() < 2) {
			data = "0" + data;
		}
		return data;
	}

	public int getResultRowIdx() {
		int idx = 19;
		idx = this.excelData[0].length - 1;
		return idx;
	}

	/**
	 * Desc : 데이터 검증 테스트
	 *
	 * @Method Name : _confirmExcelData
	 * @param data
	 */
	public void _confirmExcelData(String[][] data) {
		for (int r = 0; r < data.length; r++) {
			for (int c = 0; c < data[0].length; c++) {
				this.log.debug("행" + r + "열" + c + " : " + data[r][c]);
			}
		}
	}

	/**
	 * <pre>
	 * 엑셀 에러 위치 기록
	 * </pre>
	 *
	 * @Method Name : errorWrite
	 * @param path
	 * @param row
	 * @param col
	 * @param errMsg
	 */
	public void errorWrite_org(String path, int row, int col, String errMsg, boolean isHeader, boolean isDone) {

		Workbook workbook = null;
		FileInputStream fis = null;
		Sheet sheet = null;
		Cell cell = null;

		try {

			fis = new FileInputStream(path);
			workbook = WorkbookFactory.create(fis);
			sheet = workbook.getSheetAt(0);

			if (isHeader) {
				// 등록 여부 컬럼 사이즈
				sheet.setColumnWidth(col, 8000);
			}

			Row eRow = sheet.getRow(row);

			// 등록여부 메세지 표시
			cell = eRow.createCell(col);
			cell.setCellValue(errMsg);
			// 기본 셀스타일
			CellStyle cellStyle2 = workbook.createCellStyle();
			cellStyle2.cloneStyleFrom(eRow.getCell(4).getCellStyle());

			// 에러 스타일
			if (!isDone) {
				cellStyle2.setFillPattern(CellStyle.SOLID_FOREGROUND);
				cellStyle2.setFillForegroundColor(HSSFColor.YELLOW.index);
			}
			cell.setCellStyle(cellStyle2);
			this.closeWorkbook(workbook, path);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * <pre>
	 *  엑셀 데이터 기록
	 * </pre>
	 *
	 * @Method Name : dataWrite
	 * @param path
	 * @param row
	 * @param col
	 * @param errMsg
	 * @param isHeader
	 * @param isDone
	 */
	public void dataWrite(String path, int row, int col, String data) {

		Workbook workbook = null;
		FileInputStream fis = null;
		Sheet sheet = null;
		Cell cell = null;

		try {

			fis = new FileInputStream(path);
			workbook = WorkbookFactory.create(fis);
			sheet = workbook.getSheetAt(0);

			Row eRow = sheet.getRow(row);

			// 등록여부 메세지 표시
			cell = eRow.getCell(col);
			cell.setCellValue(data);
			// 기본 셀스타일

			this.closeWorkbook(workbook, path);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 *
	 * @Method Name : closeWorkbook
	 * @param workbook
	 * @param path
	 */
	public void closeWorkbook(Workbook workbook, String path) {

		try {
			this.fileOut = new FileOutputStream(path);
			workbook.write(this.fileOut);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {

				if (null != this.fileOut) {
					this.fileOut.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * <pre>
	 * 마지막 소수점이 .0 이면 제거
	 * </pre>
	 *
	 * @Method Name : removeRoundZero
	 * @param str
	 * @return
	 */
	private String removeRoundZero(String str) {

		String lastNum = str.substring(str.length() - 2, str.length());
		if (lastNum.equals(".0")) {
			str = str.substring(0, str.length() - 2);
		}

		return str;
	}

	public static void main(String[] args) {
		try {
			//			CommonFileUtils.createFolder("C:\\Users\\Administrator\\Desktop\\새 폴더", 83);
			//			CommonFileUtils.copyToFileByYunBun("C:\\Users\\Administrator\\Desktop\\새 폴더\\행정박물_최종선별사진_연번순", "-", "C:\\Users\\Administrator\\Desktop\\새 폴더");
			ExcelImporter test = new ExcelImporter("C:\\nursing\\", "a.xls");
			String sss[][] = test.getExcelDataArray();
//			FlMuseumVO vo = test.arrayToFlMuseumVO(test.getExcelDataArray(), 0);

			System.out.println();
			//
			//			int i = 2;
			//			for (String[] strings : sss) {
			//				if (null != strings[7]) {
			//					strings[7] = strings[7].replaceAll("\\.", "-");
			//					//					test.dataWrite("C:\\uacExcelReg\\기록관 행정박물 보유목록_20131209_사료관리시스템등록용.xls", i, 7, strings[7]);
			//				}
			//				i++;
			//				// if (i == 763) {
			//				// break;
			//				// }
			//			}
			//			System.out.println("clear");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
