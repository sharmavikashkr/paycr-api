package com.paycr.common.util;

public class StateHelper {

	public static String getStateForCode(String code) {
		switch (code) {
		case "1":
		case "01":
			return "Jammu and Kashmir (01)";
		case "2":
		case "02":
			return "Himachal Pradesh (02)";
		case "3":
		case "03":
			return "Punjab (03)";
		case "4":
		case "04":
			return "Chandigarh (04)";
		case "5":
		case "05":
			return "Uttaranchal (05)";
		case "6":
		case "06":
			return "Haryana (06)";
		case "7":
		case "07":
			return "Delhi (07)";
		case "8":
		case "08":
			return "Rajasthan (08)";
		case "9":
		case "09":
			return "Uttar Pradesh (09)";
		case "10":
			return "Bihar (10)";
		case "11":
			return "Sikkim (11)";
		case "12":
			return "Arunachal Pradesh (12)";
		case "13":
			return "Nagaland (13)";
		case "14":
			return "Manipur (14)";
		case "15":
			return "Mizoram (15)";
		case "16":
			return "Tripura (16)";
		case "17":
			return "Meghalaya (17)";
		case "18":
			return "Assam (18)";
		case "19":
			return "West Bengal (19)";
		case "20":
			return "Jharkhand (20)";
		case "21":
			return "Orissa (21)";
		case "22":
			return "Chhattisgarh (22)";
		case "23":
			return "Madhya Pradesh (23)";
		case "24":
			return "Gujarat (24)";
		case "25":
			return "Daman and Diu (25)";
		case "26":
			return "Dadra and Nagar Haveli (26)";
		case "27":
			return "Maharashtra (27)";
		case "29":
			return "Karnataka (29)";
		case "30":
			return "Goa (30)";
		case "31":
			return "Lakshadweep (31)";
		case "32":
			return "Kerala (32)";
		case "33":
			return "Tamil Nadu (33)";
		case "34":
			return "Pondicherry (34)";
		case "35":
			return "Andaman and Nicobar Islands (35)";
		case "36":
			return "Telangana (36)";
		case "37":
			return "Andhra Pradesh (37)";
		default:
			return "";
		}
	}

}
