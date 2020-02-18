package com.sist.food;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FoodParser {
	/* FOODINFO
	 NO       NUMBER         
	NAME     VARCHAR2(200)  
	GRADE    NUMBER(4,2)    
	TAG      VARCHAR2(1000) 
	ADDR     VARCHAR2(1000) 
	MAPX     VARCHAR2(2000) 
	MAPY     VARCHAR2(1000) 
	TEL      VARCHAR2(200)  
	OPNHR    VARCHAR2(100) 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FoodDAO dao = new FoodDAO();
		int maxPage = 1;
		String name = "";
		String tag;
		String addr;
		double mapX;
		double mapY;
		String tel;
		String opnhr;
		
		int cnt = 0;
		try {
			dao.getConnection();
		} catch (Exception e) {
			System.out.println("error");
		}
		try {
			for(int i=0; i<maxPage; i++) {
				Document listDoc = Jsoup.connect("https://www.tripadvisor.co.kr/Restaurants").get();

				Elements detailList = listDoc.select("div.listing_title a");

				for(int j=0; j<detailList.size(); j++) {

					Document detailDoc = Jsoup.connect("https://www.tripadvisor.co.kr"+detailList.get(j).attr("href")).get();
					FoodVO vo = new FoodVO();
					

					vo.setNo(i+j);
					System.out.println(i+"-"+j+" : ");

					
					try {
						name = detailDoc.selectFirst("h1#HEADING").text();
						if(dao.hasFoodVOName(name)) {
							System.out.println("Foodinfo has "+name);
							continue;

						}

						vo.setName(name);

					} catch (Exception e) {
					
						continue;


					}
					
					try {
						tag = detailDoc.selectFirst("span.is-hidden-mobile div.detail").text();
						if(tag.contains("더 보기")) {
							tag = tag.replace("더 보기", "");
						}
						vo.setTag(tag.trim());
					} catch (Exception e) {
						continue;
					}
					
					try {
						vo.setAddr(detailDoc.selectFirst("div.is-hidden-mobile span.detail").text());
					} catch (Exception e) {
						continue;
					}
					
					try {
						String html = detailDoc.html();
						int idx = html.indexOf("\"name\":\""+name+"\"");
						html = html.substring(idx);
						idx = html.indexOf("coords");
						html = html.substring(idx);
						idx = html.indexOf("},{");
						html = html.substring(0, idx);
						String[] maps = html.split("\"");
						String map = maps[maps.length-1];
						maps = map.split(",");
						mapX = Double.parseDouble(maps[0]);
						mapY = Double.parseDouble(maps[1]);
						vo.setMapX(mapX);
						vo.setMapY(mapY);
					} catch (Exception e) {
						continue;
					}
					cnt++;
					System.out.print(cnt);
					dao.printFoodVOData(vo);
					dao.foodInsert(vo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Crawling end");
	}
	
}
