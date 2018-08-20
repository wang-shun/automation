package com.gome.test.api.verify;
import com.gome.test.api.utils.DBUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gome.test.api.verify.JsonVerify;
import com.gome.test.api.verify.Verify;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.testng.Assert;

public class SamplesVerify extends JsonVerify {

	@Verify(description = "延迟查询db获取response")
	public void delayQuerySetResponse(long millis, String sqlId)
			throws Exception {
		Thread.sleep(millis);
		Map<Integer, String> meta = new HashMap<Integer, String>();
		List<String[]> results = DBUtils.querySQL(sqlId, meta);
		List<Map<String, String>> l = new ArrayList<Map<String, String>>();
		for (int i = 0; i < results.size(); ++i) {
			Map<String, String> m = new HashMap<String, String>();
			String[] strs = results.get(i);
			for (int j = 0; j < strs.length; ++j) {
				m.put(meta.get(j), strs[j]);
			}
			l.add(m);
		}
		ObjectMapper objMapper = new ObjectMapper();
		ObjectNode objNode = (ObjectNode) objMapper.readTree(response);
		objNode.put(sqlId, objMapper.readTree(objMapper.writeValueAsString(l)));
		response = objMapper.writeValueAsString(objNode);
	}

	@Verify(description = "oracle验证示例")
	public void verifyOracleSample(String brandId) throws Exception {
		List<String[]> result = DBUtils.querySQL("query_brand");
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.get(0)[0], brandId);
	}
	
	@Verify(description = "mysql验证示例")
	public void verifyMysqlSample(String appId) throws Exception {
		List<String[]> result = DBUtils.querySQL("query_app");
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.get(0)[0],appId);
	}
}
