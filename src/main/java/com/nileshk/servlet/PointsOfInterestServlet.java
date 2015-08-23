package com.nileshk.servlet;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.function.Function;

@WebServlet(name = "pointsOfInterestServlet", urlPatterns = "/points_of_interest")
public class PointsOfInterestServlet extends HttpServlet {

	private static final Logger logger = Logger.getLogger(PointsOfInterestServlet.class);

	private static final long serialVersionUID = 8174793990899887809L;

	static {
		logger.debug("PointsOfInterestServlet loaded");
	}


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doSelect(response, "SELECT row_to_json(fc)\n"
				+ "  FROM ( SELECT 'FeatureCollection' AS type, array_to_json(array_agg(f)) AS features\n"
				+ "           FROM ( SELECT 'Feature' AS type,\n"
				+ "                         ST_AsGeoJSON(lg.the_geom)::JSON AS geometry,\n"
				+ "                         row_to_json((SELECT l FROM (SELECT name) AS l)) AS properties\n"
				+ "                  FROM points_of_interest AS lg ) AS f )  AS fc\n", (rs) -> {
			try {
				return rs.getString(1);
			} catch (SQLException e) {
				logger.error("Error executing SQL statement", e);
				throw new RuntimeException(e);
			}
		});
	}

	private void doSelect(HttpServletResponse response, String sql, Function<ResultSet, String> rowHandler) {
		response.setContentType("application/json");
		try (PrintWriter out = response.getWriter()) {
			String url = "jdbc:postgresql://localhost/nil";
			Properties props = new Properties();
			//props.setProperty("user","fred");
			//props.setProperty("password","secret");
			//props.setProperty("ssl","true");
			try {
				Class.forName("org.postgresql.Driver");
			} catch (ClassNotFoundException e) {
				logger.error("Error loading PostgreSQL driver", e);
				out.println("CLASS_NOT_FOUND ERROR");
			}
			try (Connection conn = DriverManager.getConnection(url, props)) {
				logger.debug("Trying to select from DB...");
				try (Statement statement = conn.createStatement()) {
					try (ResultSet rs = statement.executeQuery(sql)) {
						rs.next();
						String str = rowHandler.apply(rs);
						out.write("{ \"result\": " + str + "}");
					}
				}
			} catch (SQLException e) {
				logger.error("Error executing SQL statement", e);
				out.println("DATABASE ERROR");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
