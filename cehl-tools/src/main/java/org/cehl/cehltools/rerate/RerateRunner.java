package org.cehl.cehltools.rerate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.collections.map.MultiKeyMap;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerStatsAllStrengths;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;


public class RerateRunner {
    public static void main( String[] args )
    {

    	SpringApplicationBuilder appBuilder = new SpringApplicationBuilder()
    	        .sources(RerateConfig.class);
    	
    	ConfigurableApplicationContext context = appBuilder.run(args);

    	RerateRunner runner = new RerateRunner();
    	runner.run2(context);
    }
    
    void run(ApplicationContext context) {
//    	   private final ResultSetExtractor<List<User>> resultSetExtractor = 
//    		        JdbcTemplateMapperFactory
//    		            .newInstance()
//    		            .addKeys("id") // the column name you expect the user id to be on
//    		            .newResultSetExtractor(User.class);\
    	
    	DataSource ds = context.getBean(DataSource.class);
    	JdbcTemplate template  = new JdbcTemplate(ds);
    	
    	String sql = "SELECT b.name, b.position, b.dob, b.prov_state, b.country, b.nationality, b.height, b.weight FROM player_ratings.player_bio b " + 
    			" group by b.name, b.dob";
    	
    	sql = "SELECT b.name, b.position, b.dob, b.prov_state, b.country, b.nationality, max(b.height) as height, max(b.weight) as weight, \r\n" + 
    			"sa.year, sa.team, sa.position, sa.gp, sa.toi, sa.goals, sa.assists, sa.assists_first, sa.assists_second, sa.points, sa.shots, sa.sh_pct,\r\n" + 
    			"sa.rush_attempt, sa.pim, sa.pen_minor, sa.pen_major, sa.pen_misconduct, sa.pen_Drawn, sa.giveaway, sa.takeaway, sa.hits, sa.shots_blocked\r\n" + 
    			"FROM player_ratings.player_bio b\r\n" + 
    			"LEFT OUTER JOIN player_ratings.player_stats_all sa ON b.name = sa.name AND b.year = sa.year AND b.team=sa.team\r\n" + 
    			"#where b.name = 'Taylor Hall'\r\n" + 
    			"group by b.name, b.dob, sa.year\r\n" + 
    			"";
    	
//    	ResultSetExtractor rse = new ResultSetExtractor() {
//
//			@Override
//			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
//				// TODO Auto-generated method stub
//				return null;
//			}
//    		
//    	};
    	
    	MultiKeyMap playerMap = new MultiKeyMap();
    	
    	RowCallbackHandler rch = new RowCallbackHandler() {
    		
    		Player currentPlayer;

			@Override
			public void processRow(ResultSet rs) throws SQLException {
			
				String name = rs.getString("name");
				LocalDate dob =  rs.getDate("dob").toLocalDate();
				
				Player player = (Player) playerMap.get(name, dob);
				
				if (player == null) {
					player = new Player();
					player.setName(name);
					player.setDob(dob);
					playerMap.put(name, dob, player);
			    }
				
				PlayerStatsAllStrengths season = new PlayerStatsAllStrengths();
				//season.setYear(rs.getInt("year"));
			
			
				//player.getSeasons().add(season);
			}
    		
    	};

    	
    	template.query(sql, rch);
    	

    			playerMap.values().stream().forEach(p -> System.out.println(p));
    			
    			
    			sql = "SELECT b.name, max(b.position) as position, max(b.dob) as dob, max(b.prov_state)as prov_state, max(b.country) as country,\r\n" + 
    					" max(b.nationality) as nationality, max(b.height) as height, max(b.weight) as weight\r\n" + 
    					"FROM player_ratings.player_bio b\r\n" + 
    					"group by b.name, b.dob\r\n" + 
    					"order by b.name, b.dob";
    	
    	   final ResultSetExtractor<List<Player>> resultSetExtractor = 
    		        JdbcTemplateMapperFactory
    		            .newInstance()
    		            .addKeys("name","dob") // the column name you expect the user id to be on
    		            .newResultSetExtractor(Player.class);
    	
    	List<Player> results = template.query(sql, resultSetExtractor);
   
    	
    	results.stream().forEach(p -> System.out.println(p));

    }
    
    void run2(ApplicationContext context) {

    	DataSource ds = context.getBean(DataSource.class);
    	JdbcTemplate template  = new JdbcTemplate(ds);

    	String sql = "SELECT b.name, max(b.position) as position, max(b.dob) as dob, max(b.prov_state)as prov_state, max(b.country) as country,\r\n" + 
    			" max(b.nationality) as nationality, max(b.height) as height, max(b.weight) as weight\r\n" + 
    			"FROM player_ratings.player_bio b\r\n" + 
    			"group by b.name, b.dob\r\n" + 
    			"order by b.name, b.dob";
    	
    	 sql = "SELECT name, dob, position, country, height, weight FROM player_ratings.player;";

    	final ResultSetExtractor<List<Player>> resultSetExtractor = 
    			JdbcTemplateMapperFactory
    			.newInstance()
    			.addKeys("name","dob") // the column name you expect the user id to be on
    			.newResultSetExtractor(Player.class);

    	List<Player> results = template.query(sql, resultSetExtractor);


    	results.stream().forEach(p -> System.out.println(p));
    }
}
