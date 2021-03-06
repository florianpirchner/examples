package eclipselink.example.jpa.employee.model;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.EmptyRecord;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.QueryRedirector;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.queries.WriteObjectQuery;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class AddressInsertQueryRedirector3 implements QueryRedirector {

	@SuppressWarnings("unchecked")
	@Override
	public Object invokeQuery(DatabaseQuery query, Record arguments, Session session) {
		WriteObjectQuery insertObjectQuery = (WriteObjectQuery) query;

		Address addr = (Address) insertObjectQuery.getObject();
		Address current = getCurrent(addr, (AbstractSession) session);

		if (current == null) {
			System.out.println(addr);
			addr.setValidFrom(new Date());
			addr.setValidUntil(getMaxDate());
			addr.setHistCurrent(true);

			insertObjectQuery.setDoNotRedirect(true);
			Object newAdr = insertObjectQuery.execute((AbstractSession) session, (AbstractRecord) arguments);
			return newAdr;
		} else {

			// update the address to be historized
			Date now = new Date();
			AbstractRecord updRow = query.getDescriptor().getObjectBuilder().buildRowForTranslation(current, (AbstractSession) session);
			DatabaseField validFromDBField = query.getDescriptor().getMappingForAttributeName("validFrom").getField();
			DatabaseField validUntilDBField = query.getDescriptor().getMappingForAttributeName("validUntil").getField();
			DatabaseField versionDBField = query.getDescriptor().getMappingForAttributeName("version").getField();
			DatabaseField histCurrentDBField = query.getDescriptor().getMappingForAttributeName("histCurrent")
					.getField();
			updRow.put(validUntilDBField, now);
			updRow.put(histCurrentDBField, false);

			UpdateObjectQuery updateObjectQuery = new UpdateObjectQuery(addr);
			updateObjectQuery.setDoNotRedirect(true);
			updateObjectQuery.setIsUserDefined(true);
			updateObjectQuery.setTranslationRow(updRow);
			updateObjectQuery.setModifyRow(updRow);
			updateObjectQuery.bindAllParameters();
			Object old = updateObjectQuery.execute((AbstractSession) session, updRow);
			System.out.println(old);

			AbstractRecord insertRow = query.getDescriptor().getObjectBuilder().buildRow(addr,
					(AbstractSession) session, WriteType.INSERT);
			insertRow.put(validFromDBField, now);
			insertRow.put(validUntilDBField, getMaxDate());
			insertRow.put(histCurrentDBField, true);
			insertRow.put(versionDBField, 0);

			ReadObjectQuery templateQuery = new ReadObjectQuery(addr.getClass());
			templateQuery.setSession((AbstractSession) session);
			Object newAddr = query.getDescriptor().getObjectBuilder().buildObject(templateQuery, insertRow);
			InsertObjectQuery _insertObjectQuery = new InsertObjectQuery(newAddr);
			_insertObjectQuery.setDoNotRedirect(true);
			_insertObjectQuery.setIsUserDefined(true);
			Object inserted = _insertObjectQuery.execute((AbstractSession) session, EmptyRecord.getEmptyRecord());
			// insertMechanism.insertObject();

			return inserted;
		}

	}

	private Address getCurrent(Address addr, AbstractSession session) {

		UnitOfWorkImpl uow = session.acquireNonSynchronizedUnitOfWork();

		ReadObjectQuery rq = new ReadObjectQuery(Address.class);

		ExpressionBuilder eb = rq.getExpressionBuilder();
		Expression exp = eb.get("id").equal(addr.getId()).and(eb.get("histCurrent").equal(true));
		rq.setSelectionCriteria(exp);
		rq.dontCheckCache();
		rq.dontMaintainCache();

		Address current = (Address) rq.executeInUnitOfWork(uow, EmptyRecord.getEmptyRecord());

		uow.clearForClose(true);
		return current;
	}

	private Date getMaxDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(2099, Calendar.DECEMBER, 31, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date maxDate = cal.getTime();
		return maxDate;
	}

}
