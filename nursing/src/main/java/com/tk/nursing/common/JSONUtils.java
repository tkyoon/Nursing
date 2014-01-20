package com.tk.nursing.common;


import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.TypeReference;

/**
 * <PRE>
 * JSON Data Type 관련 공통 유틸리티 클래스
 * </PRE>
 *
 * @author JCONE
 */
public class JSONUtils {

	/**
	 * 객체를 JSON 문자열로 변환하여 반환
	 *
	 * @param o JSON 문자열로 변환할 객체
	 * @return 변환된 JSON 문자열
	 * 만약 변환중 예외 발생시 White Space 반환
	 */
	public static String toJSON(Object o) {
		ObjectMapper mapper = new ObjectMapper();

		// 의미있는 값을 가진 변수만 변환, null 또는 White Space 제외
		mapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
		// mapper.setSerializationInclusion(Inclusion.NON_DEFAULT);
		// mapper.setSerializationInclusion(Inclusion.NON_NULL);
		// mapper.setSerializationInclusion(Inclusion.NON_EMPTY);

		String rtnStr = StringUtils.EMPTY;

		try {
			rtnStr = mapper.writer().writeValueAsString(o);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rtnStr;
	}

	/**
	 * JSON 문자열을 객체로 변환하여 반환
	 * <p>
	 * <blockquote>
	 *
	 * <pre>
	 * <b>[Code Sampe]</b>
	 * List&lt;Map&gt; listMap = fromJSON(new TypeReference&lt;List&lt;Map&gt;&gt;() {}, jsonStr);
	 * Map map = fromJSON(new TypeReference&lt;Map&gt;() {}, jsonStr);
	 * </pre>
	 *
	 * </blockquote>
	 * <p>
	 *
	 * @param typeReference 변환할 객체 형식
	 * @param jsonStr 변환 되어질 JSON 문자열
	 * @return 변환된 객체, 예외발생시 null 반환
	 */
	public static <T> T fromJSON(final TypeReference<T> typeReference, final String jsonStr) {
		ObjectMapper mapper = new ObjectMapper();

		T rtsObj = null;

		try {
			rtsObj = mapper.readValue(jsonStr, typeReference);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rtsObj;
	}

	/**
	 * 객체를 다른 객체로 변환하여 반환
	 * <p>
	 * <blockquote>
	 *
	 * <pre>
	 * <b>[Code Sampe]</b>
	 * ResearchVO voOne = new ResearchVO();
	 * voOne.setPlanStatusNm("@@planStatusNm");
	 * voOne.setRsTypeCdNm("@@rsTypeCdNm");
	 *
	 * Map rtsMap = JSONUtils.convert(voOne, Map.class);
	 * </pre>
	 *
	 * </blockquote>
	 * <p>
	 *
	 * @param srcObj	대상 객체
	 * @param destClass	변환될 Class
	 * @return
	 */
	public static <T> T convert(Object srcObj, Class<T> destClass) {
		T rtsObj = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Inclusion.NON_DEFAULT);
		mapper.setVisibilityChecker(mapper.getVisibilityChecker().withFieldVisibility(Visibility.ANY));
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try {
			rtsObj = (T) mapper.convertValue(srcObj, destClass);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rtsObj;
	}

	/*
	// @@ 01
	public static void main(String[] args) throws Exception {
	    String test = "{ \"foo\":\"bar\", \"bim\":\"baz\" }";
	    ObjectMapper mapper = new ObjectMapper();
	    HashMap map = mapper.readValue(new StringReader(test), HashMap.class);
	    System.out.println(map);
	    Test1 test1 = mapper.convertValue(map, Test1.class);
	    System.out.println(test1);
	}

	// @@ 02
	JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, Foo.class)
	List<Foo> list = mapper.readValue(new File("input.json"), type);

	// @@ 03
	public static <T> T f(T paramT) {
		T data = null;
		return data;
	}
	*/
}