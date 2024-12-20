package com.lxrtalk.texas.utils.filters;

import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletResponse;

public class FilterUtil {
	private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

	public static void setResponse(String json, HttpServletResponse response) {
		response.setContentType(CONTENT_TYPE);
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			pw.write(json);
			pw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pw != null)
					pw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
