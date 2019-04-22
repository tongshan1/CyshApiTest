package framework.listener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import config.BaseConfig;
import framework.report.Data;
import framework.report.Detail;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.xml.XmlSuite;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import yamlModel.Request;

public class ReportListener implements IReporter {

	private Path reportDir = BaseConfig.ReportRoot;

	private Path reportDetailDir;

	private String indexName = "index.html";
	private String detailNameBase = "detail_%s.html";
	
	private String templatePath = BaseConfig.ReportTemplate.toString();

	private String indexTemplatePath = BaseConfig.IndexTemplate.toString();

	private int testsPass = 0;

	private int testsFail = 0;

	private int testsSkip = 0;
	
	private String beginTime;
	
	private long totalTime;
	
	private String name;

	public ReportListener(){

		try{
			if(!Files.exists(reportDir)){
				Files.createDirectory(reportDir);
			}

			reportDetailDir = Paths.get(reportDir.toString(), "detail");

			if(!Files.exists(reportDetailDir)){
				Files.createDirectory(reportDetailDir);
			}
		}catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public ReportListener(String name){
		this.name = name;
		if(this.name==null){
			SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmssSSS");
			this.name = formatter.format(System.currentTimeMillis());
		}
	}

	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {

		for (ISuite suite : suites) {
			List<ITestResult> list = new ArrayList<ITestResult>();
			Map<String, ISuiteResult> suiteResults = suite.getResults();
			for (ISuiteResult suiteResult : suiteResults.values()) {
				ITestContext testContext = suiteResult.getTestContext();
				IResultMap passedTests = testContext.getPassedTests();
				testsPass = testsPass + passedTests.size();
				IResultMap failedTests = testContext.getFailedTests();
				testsFail = testsFail + failedTests.size();
				IResultMap skippedTests = testContext.getSkippedTests();
				testsSkip = testsSkip + skippedTests.size();
				IResultMap failedConfig = testContext.getFailedConfigurations();
				list.addAll(this.listTestResult(passedTests));
				list.addAll(this.listTestResult(failedTests));
				list.addAll(this.listTestResult(skippedTests));
				list.addAll(this.listTestResult(failedConfig));
			}
			this.sort(list);
			this.outputResult(list);
		}

	}

	private ArrayList<ITestResult> listTestResult(IResultMap resultMap) {
		Set<ITestResult> results = resultMap.getAllResults();
		return new ArrayList<ITestResult>(results);
	}

	private void sort(List<ITestResult> list) {
		Collections.sort(list, new Comparator<ITestResult>() {
			@Override
			public int compare(ITestResult r1, ITestResult r2) {
				if (r1.getStartMillis() > r2.getStartMillis()) {
					return 1;
				} else {
					return -1;
				}
			}
		});
	}

	private Map<String , List<Data>> initResult(List<ITestResult> list){

		Map<String , List<Data>> mapInfo = new HashMap<String , List<Data>>();

		int index =0;
		for (ITestResult result : list) {

			String testCaseName = (String) result.getAttribute("testCaseName");

			Request request = (Request) result.getAttribute("request");

			long spendTime = result.getEndMillis() - result.getStartMillis();
			totalTime += spendTime;
			String status = this.getStatus(result.getStatus());
			List<String> log = Reporter.getOutput(result);
			String response = request.getResponse().getBody().asString();
			log.add("期望值：");
			log.add(request.getValidators().toString());
			log.add("返回值：");
			log.add(response);
			log.add("其他：");
			for (int i = 0; i < log.size(); i++) {
				log.set(i, log.get(i).replaceAll("\"", "\\\\\""));
			}
			Throwable throwable = result.getThrowable();
			if(throwable!=null){
				log.add(throwable.toString().replaceAll("\"", "\\\\\""));
				StackTraceElement[] st = throwable.getStackTrace();
				for (StackTraceElement stackTraceElement : st) {
					log.add(("    " + stackTraceElement).replaceAll("\"", "\\\\\""));
				}
			}
			SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss.SSS");
			if(index == 0){
				beginTime = formatter.format(new Date(result.getStartMillis()));
				index++;
			}
			String testBeginTime = formatter.format(new Date(result.getStartMillis()));

			Data data = new Data();
			data.setTestBeginTime(testBeginTime);
			data.setSpendTime(spendTime);
			data.setStatus(status);
			data.setUrl(request.getUrl());
			data.setMethod(request.getMethod());
			Map<String, Object> params = new HashMap<>();
			params.put("path", request.getPath());
			params.put("headers", request.getHeaders());
			params.put("query", request.getQuery());
			params.put("body", request.getBody());
			data.setParams(params);


			data.setLog(log);
			if(mapInfo.containsKey(testCaseName)){
				List<Data> datas = mapInfo.get(testCaseName);
				datas.add(data);
				mapInfo.put(testCaseName, datas);

			}else{
				List<Data> datas = new ArrayList<Data>();
				datas.add(data);
				mapInfo.put(testCaseName,datas );
			}

		}

		return mapInfo;
	}

	private void outputResult (List<ITestResult> list){
		try {
			Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

			Map<String , List<Data>> mapInfo = initResult(list);
			Map<String, Object> indexResult = new HashMap<>();
			List<Detail> details = new ArrayList<>();

			int testPass = 0;
			int testFail = 0;

			for(String testName:mapInfo.keySet()) {

				List<Data> datas = mapInfo.get(testName);

				int pass = 0;
				int skip = 0;
				int fail = 0;


				long testTotalTime = 0;
				String testBeginTime= "";
				int index = 0;
				for(Data data: datas){

					if (index == 0) {

						testBeginTime = data.getTestBeginTime();
						index++;
					}
					testTotalTime += data.getSpendTime();
					if (data.getStatus() == "成功"){
						pass++;
					}else if(data.getStatus() =="失败"){
						fail++;
					}else {
						skip++;
					}
				}
				Map<String, Object> result = new HashMap<>();
				result.put("testName", testName);
				result.put("testPass", pass);
				result.put("testFail", fail);
				result.put("testSkip", skip);
				result.put("testAll", pass+skip+fail);
				result.put("beginTime", testBeginTime);
				result.put("totalTime", testTotalTime + "ms");
				result.put("testResult", datas);

				String template = this.read(templatePath);
				String detailName = String.format(detailNameBase, testName.replaceAll(" ", "_"));
				Path reportDetailPath = Paths.get(reportDetailDir.toString(), detailName);
				BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(reportDetailPath.toString())), "UTF-8"));
				template = template.replaceFirst("\\$\\{resultData\\}", Matcher.quoteReplacement(gson.toJson(result)));
				output.write(template);
				output.flush();
				output.close();

				Detail detail = new Detail();
				detail.setTestName(testName);
				detail.setCaseNumber(pass+skip+fail);
				if(fail>0){
					detail.setStatus("失败");
					testFail++;
				}else {
					testPass++;
					detail.setStatus("成功");
				}
				String url = String.format("./detail/%s", detailName);
				detail.setUrl(url);
				detail.setSpendTime(testTotalTime+"ms");
				details.add(detail);


			}
			indexResult.put("testAll", testFail+testPass);
			indexResult.put("testPass", testPass);
			indexResult.put("testFail", testFail);
			indexResult.put("beginTime", beginTime);
			indexResult.put("totalTime", totalTime+"ms");
			indexResult.put("testResult", details);

			String template = this.read(indexTemplatePath);
			Path reportIndexPath = Paths.get(reportDir.toString(), indexName);
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(reportIndexPath.toString())), "UTF-8"));
			template = template.replaceFirst("\\$\\{resultData\\}", Matcher.quoteReplacement(gson.toJson(indexResult)));
			output.write(template);
			output.flush();
			output.close();

		}catch (IOException e) {
			e.printStackTrace();
		}finally {

		}

	}

	private String getStatus(int status) {
		String statusString = null;
		switch (status) {
		case 1:
			statusString = "成功";
			break;
		case 2:
			statusString = "失败";
			break;
		case 3:
			statusString = "跳过";
			break;
		default:
			break;
		}
		return statusString;
	}

	private String read(String path) {
		File file = new File(path);
		InputStream is = null;
		StringBuffer sb = new StringBuffer();
		try {
			is = new FileInputStream(file);
			int index = 0;
			byte[] b = new byte[1024];
			while ((index = is.read(b)) != -1) {
				sb.append(new String(b, 0, index));
			}
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
