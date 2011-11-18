package edu.zjut.time;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeMap;

import edu.zjut.common.data.TimePeriod;
import edu.zjut.common.data.TimeSeriesCollection;
import edu.zjut.common.data.TimeSeriesData;
import edu.zjut.common.data.TimeType;

import au.com.bytecode.opencsv.CSVReader;

public class DataLoader {

	/**
	 * 按城区划分的时间序列数据
	 * 
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public static TimeSeriesCollection loadHZDataset(int col, boolean avg) {
		try {

			SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");

			// String[] areaList = { "上城区", "下城区", "拱墅区", "西湖区", "滨江区", "江干区",
			// "之江区", "下沙区" };

			String[] areaList = { "下城区", "拱墅区", "西湖区", "滨江区", "江干区", "之江区",
					"下沙区" };
			// String[] areaList = { "西湖区", "滨江区" };

			TreeMap<String, Integer> areaMap = new TreeMap<String, Integer>();
			for (int i = 0; i < areaList.length; i++) {
				areaMap.put(areaList[i], i);
			}

			TimeSeriesData[] tsArr = new TimeSeriesData[areaList.length];
			for (int i = 0; i < areaList.length; i++) {
				tsArr[i] = new TimeSeriesData("", areaList[i]);
			}

			String infile = "data/sale_data_by_date.csv";

			CSVReader reader = new CSVReader(new FileReader(infile));
			List<String[]> list1 = reader.readAll();

			ArrayList<TreeMap<String, Integer>> priceMapList = new ArrayList<TreeMap<String, Integer>>();
			ArrayList<TreeMap<String, Integer>> countMapList = new ArrayList<TreeMap<String, Integer>>();
			for (int i = 0; i < areaList.length; i++) {
				priceMapList.add(new TreeMap<String, Integer>());
				countMapList.add(new TreeMap<String, Integer>());
			}

			for (int i = 0; i < list1.size(); i++) {
				String datastr = list1.get(i)[0];
				String areastr = list1.get(i)[1];
				String pricestr = list1.get(i)[col];

				// if (pricestr.equals("0"))
				// continue;
				if (areaMap.get(areastr) == null)
					continue;

				java.util.Date date = format.parse(datastr);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				c.get(Calendar.YEAR);

				String month = c.get(Calendar.YEAR) + "-"
						+ (c.get(Calendar.MONTH) + 1);
				int areaIndex = areaMap.get(areastr);
				int price = Integer.parseInt(pricestr);

				Integer sum = priceMapList.get(areaIndex).get(month);
				Integer count = countMapList.get(areaIndex).get(month);
				if (sum == null) {
					priceMapList.get(areaIndex).put(month, price);
					countMapList.get(areaIndex).put(month, 1);
				} else {
					priceMapList.get(areaIndex).put(month, price + sum);
					countMapList.get(areaIndex).put(month, count + 1);
				}
			}

			for (int i = 0; i < areaList.length; i++) {
				for (String month : priceMapList.get(i).keySet()) {
					int sum = priceMapList.get(i).get(month);
					int count = countMapList.get(i).get(month);
					String[] toks = month.split("-");

					int y = Integer.parseInt(toks[0]);
					int m = Integer.parseInt(toks[1]);

					// 计算均价
					if (avg)
						tsArr[i].add(new TimePeriod(y, m), 1.0f * sum / count);
					else
						tsArr[i].add(new TimePeriod(y, m), 1.0f * sum);
				}
			}

			TimeSeriesCollection dataset = new TimeSeriesCollection(TimeType.MONTH);

			for (int i = 0; i < areaList.length; i++) {
				dataset.addSeries(tsArr[i]);
			}

			return dataset;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
