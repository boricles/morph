package es.upm.fi.dia.oeg.morph.relational.test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.easymock.EasyMock;

import es.upm.fi.dia.oeg.morph.relational.RelationalModel;

public class MockRelationalModel implements RelationalModel
{

	@Override
	public void configure(Properties props) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException
	{
		
	}

	@Override
	public ResultSet query(String query) throws SQLException
	{
		//IMockBuilder<ResultSet> rsControl = EasyMock.createMockBuilder(ResultSet.class);
		
		
		
		ResultSet rs = null;
		if (query.contains("from dept"))
			rs = example1();
		else if (query.contains("from likes"))
			rs = example2();
		else if (query.contains("from emp"))
			rs = example3();
		EasyMock.replay(rs);
		return rs;
	}
	
	private ResultSet example1() throws SQLException
	{
		ResultSet rs = EasyMock.createMock(ResultSet.class);
		EasyMock.expect(rs.next()).andReturn(true);
		EasyMock.expect(rs.getString("deptId")).andReturn("_:Department10");
		EasyMock.expect(rs.getString("deptno")).andReturn("10");
		EasyMock.expect(rs.getString("dname")).andReturn("APPSERVER");
		EasyMock.expect(rs.getString("loc")).andReturn("NEW YORK");
		EasyMock.expect(rs.next()).andReturn(false);
		rs.close();
		return rs;
		
	}
	private ResultSet example2() throws SQLException
	{
		ResultSet rs = EasyMock.createMock(ResultSet.class);
		EasyMock.expect(rs.next()).andReturn(true);
		EasyMock.expect(rs.getString("empId")).andReturn("<xyz.com/emp/7369>");
		EasyMock.expect(rs.getString("empLikes")).andReturn("<xyz.com/emp/likes/Playing>");
		EasyMock.expect(rs.getString("likedObj")).andReturn("Soccer");
		EasyMock.expect(rs.next()).andReturn(true);
		EasyMock.expect(rs.getString("empId")).andReturn("<xyz.com/emp/7369>");
		EasyMock.expect(rs.getString("empLikes")).andReturn("<xyz.com/emp/likes/Watching>");
		EasyMock.expect(rs.getString("likedObj")).andReturn("Basketball");
		EasyMock.expect(rs.next()).andReturn(false);
		rs.close();
		return rs;
		
	}
	private ResultSet example3() throws SQLException
	{
		ResultSet rs = EasyMock.createMock(ResultSet.class);
		EasyMock.expect(rs.next()).andReturn(true);
		EasyMock.expect(rs.getString("empURI")).andReturn("<xyz.com/emp/7369>");
		EasyMock.expect(rs.getString("empno")).andReturn("7369");
		EasyMock.expect(rs.getString("ename")).andReturn("SMITH");
		EasyMock.expect(rs.getString("jobTypeURI")).andReturn("<xyz.com/emp/job/CLERK>");
		EasyMock.expect(rs.getString("job")).andReturn("CLERK");
		EasyMock.expect(rs.getString("deptno")).andReturn("10");
		EasyMock.expect(rs.getString("empTypeURI")).andReturn("<xyz.com/emp/etype/PARTTIME>");
		EasyMock.expect(rs.getString("etype")).andReturn("PARTTIME");
		EasyMock.expect(rs.getString("graphURI")).andReturn("<xyz.com/graph/CLERK/PARTTIME>");
		EasyMock.expect(rs.next()).andReturn(false);
		rs.close();
		return rs;
		
	}
}
