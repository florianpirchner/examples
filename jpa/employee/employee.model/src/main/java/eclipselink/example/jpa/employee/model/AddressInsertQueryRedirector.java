package eclipselink.example.jpa.employee.model;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.EmptyRecord;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.JPQLCall;
import org.eclipse.persistence.queries.QueryRedirector;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class AddressInsertQueryRedirector implements QueryRedirector {

	@Override
	public Object invokeQuery(DatabaseQuery query, Record arguments, Session session) {
		InsertObjectQuery insertObjectQuery = (InsertObjectQuery) query;
		Address addr = (Address) insertObjectQuery.getObject();

		if(addr.getVersion() > 1) {
			throw new IllegalArgumentException("Version is greater then 1");
		}
		
		Date maxDate = getMaxDate();
		addr.setValidUntil(maxDate);

		Call call = new JPQLCall("select a from Address a where a.id = :id and a.validUntil = :maxDate");
		ReadObjectQuery readQuery = new ReadObjectQuery(Address.class, call);
		readQuery.addArgument("id", String.class);
		readQuery.addArgument("maxDate", Date.class);
		readQuery.addArgumentValue(addr.getId());
		readQuery.addArgumentValue(maxDate);
		Address oldAddress = (Address) readQuery.execute((AbstractSession) session, EmptyRecord.getEmptyRecord());
		if (oldAddress != null) {
			oldAddress.setValidUntil(addr.getValidFrom());
			oldAddress.setHistCurrent(false);
			UpdateObjectQuery updateObjectQuery = new UpdateObjectQuery(oldAddress);
			updateObjectQuery.setDoNotRedirect(true);
			updateObjectQuery.execute((AbstractSession) session, EmptyRecord.getEmptyRecord());
		}

		insertObjectQuery.setDoNotRedirect(true);
		return insertObjectQuery.execute((AbstractSession) session, (AbstractRecord) arguments);
	}

	private Date getMaxDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(2099, Calendar.DECEMBER, 31, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date maxDate = cal.getTime();
		return maxDate;
	}

}
