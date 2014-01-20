package com.tk.nursing;

import com.tk.nursing.common.Common;
import com.tk.nursing.excel.ExcelImporter;

/**
 *
 */
public class App extends Common {

	public App(){
		Common.loadXmlProperties();
	}
	public static void main(String[] args) {
		App app = new App();
//		app.loadExcel();
		app.test();

	}

	public void loadExcel() {
		log.debug("log test");
		try {
			ExcelImporter ei = new ExcelImporter("c:\\nursing\\", "a.xls");

			String eData[][] = ei.getExcelDataArray();
			//log.debug(eData[0][0]);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void test(){
		String a = "1,2,4,5,13";
		log.debug("{}", a.contains("3"));

		Common.properties.get("is.holiday.lastmonth.lastday");
		log.debug("{}", Common.properties.get("is.holiday.lastmonth.lastday"));
	}

	public String checkDayShift(boolean isHolidayYesterday, boolean isHolidayToday,  String today){

		return "";

	}


}
