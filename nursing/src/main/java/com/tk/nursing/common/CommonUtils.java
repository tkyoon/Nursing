package com.tk.nursing.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.type.TypeReference;

import sun.misc.BASE64Decoder;

/**
 * <PRE>
 * 공통 유틸리티 클래스
 * </PRE>
 *
 * @author JCONE
 */
@SuppressWarnings("rawtypes")
public class CommonUtils {

	/**
	 * <PRE>
	 * - Object 변환 유틸
	 * - 변환할 VO 클래스를 변환될 VO 클래스로 값을 옮길 경우
	 * - Map 인스턴스를 VO 클래스로 변경할 경우
	 * - 매칭이 되지 않는 값은 무시가 된다.
	 * EX) 변환할 A VO 클래스에는 값이 있으나 변환될 B VO 클래스에는 없을 경우 그 값은 무시됨
	 * </PRE>
	 *
	 * @param sourceObj
	 *            변환할! 오브젝트 인스턴스
	 * @param destClass
	 *            변환될! 오브젝트 클래스
	 * @return 변환된! 오브젝트 클래스 인스턴스
	 */
	public static <T> T ObjToObj(Object sourceObj, Class<T> destClass) {
		return JSONUtils.convert(sourceObj, destClass);
	}

	/**
	 * <PRE>
	 * - Java Object to Map
	 * - 단, White Space, null 값은 제외하고 가공한다.
	 * - ref : http://wiki.fasterxml.com/JacksonInFiveMinutes/
	 * </PRE>
	 *
	 * @param vo
	 *            Value Object (VO, DTO, POJO etc ...)
	 * @return 가공된 Map
	 */
	public static Map<?, ?> voToMap(Object srcVO) {
		return JSONUtils.convert(srcVO, Map.class);
	}

	/**
	 * <PRE>
	 * - JSON 문자열을 <List<Map>> Object 형태로 가공하여 반환
	 * - ref : http://wiki.fasterxml.com/JacksonInFiveMinutes/
	 * </PRE>
	 *
	 * @param jsonString
	 *            변환할 Json 문자열
	 * @return 가공된 <List<Map>> Object
	 * @throws IOException
	 * @throws JsonParseException
	 */
	public static List<Map> jsonStringToListMap(String jsonString) {
		return JSONUtils.fromJSON(new TypeReference<List<Map>>() {
		}, jsonString);
	}

	/**
	 * <PRE>
	 * - JSON 문자열을 Map[] Object 형태로 가공하여 반환
	 * - ref : http://wiki.fasterxml.com/JacksonInFiveMinutes/
	 * </PRE>
	 *
	 * @param jsonString
	 *            Map[] 객체로 변환할 Json 문자열
	 * @return 가공된 Map[] Object
	 */
	public static Map[] jsonStringToArrayMap(String jsonString) {
		return JSONUtils.fromJSON(new TypeReference<Map[]>() {
		}, jsonString);
	}

	/**
	 * <PRE>
	 * - JSON 문자열을 Map Object 형태로 가공하여 반환
	 * - ref : http://wiki.fasterxml.com/JacksonInFiveMinutes/
	 * </PRE>
	 *
	 * @param jsonString
	 *            Map 객체로 변환할 Json 문자열
	 * @return 가공된 Map Object
	 * @throws IOException
	 * @throws JsonParseException
	 */
	public static Map jsonStringToMap(String jsonString) throws JsonParseException, IOException {
		return JSONUtils.fromJSON(new TypeReference<Map>() {
		}, jsonString);
	}

	/**
	 * <pre>
	 * List&lt;Map&gt; 형태의 객체를 Json 문자열로 변환
	 * ref : http://wiki.fasterxml.com/JacksonInFiveMinutes/
	 * </pre>
	 *
	 * @param listMap
	 *            변환할 List<Map> 객체
	 * @return 변환된 Json 문자열
	 */
	public static String listMapToJsonString(List<Map> paramListMap) {
		return JSONUtils.toJSON(paramListMap);
	}

	/**
	 * <pre>
	 * Map 형태의 객체를 Json 문자열로 변환
	 * http://wiki.fasterxml.com/JacksonInFiveMinutes/
	 * </pre>
	 *
	 * @param map
	 *            변환할 Map 객체
	 * @return 변환된 Json 문자열
	 */
	public static String mapToJsonString(Map paramMap) {
		return JSONUtils.toJSON(paramMap);
	}

	/**
	 * <pre>
	 * vo객체를 jsonString으로 변환
	 * </pre>
	 *
	 * @param vo
	 * @return
	 */
	public static String voToJsonString(Object vo) {
		return JSONUtils.toJSON(voToMap(vo));
	}

	/**
	 * <PRE>
	 * - HTML TAG 변환
	 * </PRE>
	 *
	 * @param includeHTMLTagString
	 *            변환할 문자열
	 * @return 변환된 문자열
	 */
	public static String replaceHTMLTag(String includeHTMLTagString) {
		String rtsString = StringUtils.EMPTY;

		if (StringUtils.isBlank(includeHTMLTagString))
			return rtsString;

		try {
			rtsString = includeHTMLTagString.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&").replaceAll("&quot;", "\"").replaceAll("&apos;", "\'");
		} catch (Exception e) {
			e.printStackTrace();
			return rtsString;
		}

		return rtsString;
	}

	/**
	 * <PRE>
	 * - 예외 메세지를 문자열로 반환
	 * </PRE>
	 *
	 * @param throwable
	 *            throw 가능한 예외 객체
	 * @return 계층구조에 해당하는 모든 예외 메세지
	 */
	public static String getExceptionMessage(Throwable throwable) {
		StringBuffer stringbuffer = new StringBuffer();

		if (throwable != null) {
			StringWriter stringwriter = new StringWriter(1024);
			PrintWriter printwriter = new PrintWriter(stringwriter);
			throwable.printStackTrace(printwriter);
			printwriter.close();
			stringbuffer.append(stringwriter.toString().replaceAll("\n", "\r\n"));
		}

		return stringbuffer.toString();
	}

	/**
	 * <PRE>
	 * - WAS의 현재 시간을 반환
	 * </PRE>
	 *
	 * @return EX) 2013-05-31T15:01:58.972Z
	 */
	public static String getTimeStamp() {
		SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.KOREA);
		return formatter.format(new Date());
	}

	/**
	 * <PRE>
	 * - PK로 사용 가능한 유니크 키 생성
	 * - UUID 32byte + prefix 3byte = 총 35byte
	 * </PRE>
	 *
	 * @param prefix
	 *            생성된 UUID을 구분해줄수 있는 업무코드
	 * @return 인자값으로 입력된 prefix와 UUID를 합친 문자열 ex)
	 *         LR-356015096E2542779E11C946DB93B952
	 */
	public static String getUUID(String prefix) {
		return prefix.toUpperCase() + "-" + UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
	}

	/**
	 * <PRE>
	 * - 최상위 클래스를 구함
	 * - Object 클래스의 바로 하위 클래스 까지 구함
	 * </PRE>
	 *
	 * @param c
	 *            최상위 클래스를 구할 클래스
	 * @return Object 클래스 바로 하위 클래스
	 */
	public static Class getParentClass(Class c) {
		Class clazz = c.getSuperclass();

		if (clazz == Object.class || clazz == null) {
			return c;
		} else {
			return getParentClass(clazz);
		}
	}

	/**
	 * <pre>
	 * seeeion.check.jsp에서 사용.요청받은 url 가공x
	 * http://localhost:8080/uac-lite/WEB-INF/jsp/specialDocument/searchFileList.uac
	 * => /specialDocument/searchFileList.uac로 변경함
	 * </pre>
	 *
	 * @param url
	 * @return
	 */
	public static String getSubStrRequestUrl(String url, String context) {
		// url =
		// "http://localhost:8080/uac-lite/WEB-INF/jsp/specialDocument/searchFileList.jsp";
		int startIdx = url.indexOf(context);
		return url.substring(startIdx + context.length(), url.length());
	}

	public static void main(String args[]) {
		// System.out.println("a="+CommonUtils.getRequestUrl(""));
	}

	/**
	 * <pre>
	 * - Byte 사이즈 계산
	 * </pre>
	 *
	 * @param size
	 * @return
	 */
	public static String byteSizeCalc(double size) {
		double LengthbyUnit = size;
		int Unit = 0;

		while (LengthbyUnit > 1024 && Unit < 5) { // 단위 숫자로 나누고 한번 나눌 때마다 Unit
													// 증가
			LengthbyUnit = LengthbyUnit / 1024;
			Unit++;
		}

		DecimalFormat df = new DecimalFormat("#,##0.00");
		String rts = df.format(LengthbyUnit);

		switch (Unit) {
		case 0:
			rts += " Bytes";
			break;
		case 1:
			rts += " KB";
			break;
		case 2:
			rts += " MB";
			break;
		case 3:
			rts += " GB";
			break;
		case 4:
			rts += " TB";
		}

		return rts;
	}

	/**
	 * <pre>
	 * BASE64Decoder
	 * </pre>
	 *
	 * @Method Name : BASE64DecoderStr
	 * @param encode
	 * @return
	 * @throws IOException
	 */
	public static String BASE64DecoderStr(String encode) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] b = decoder.decodeBuffer(encode);
		return new String(b);
	}

}