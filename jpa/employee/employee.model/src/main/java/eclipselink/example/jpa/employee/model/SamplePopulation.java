/*******************************************************************************
 * Copyright (c) 2010-2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  dclarke - Employee Demo 2.4
 ******************************************************************************/
package eclipselink.example.jpa.employee.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.EmptyRecord;
import org.eclipse.persistence.queries.ReadObjectQuery;

/**
 * Examples illustrating the use of JPA with the employee domain
 * eclipselink.example.jpa.employee.model.
 * 
 * @see eclipselink.example.jpa.employee.test.model.JavaSEExampleTest
 * 
 * @author dclarke
 * @since EclipseLink 2.4
 */
public class SamplePopulation {

	/**
	 * Create the specified number of random sample employees.
	 */
	public void createNewEmployees(EntityManager em, int quantity) {
		Address adrV1 = createAddress();
		em.persist(adrV1);
		for (int index = 0; index < quantity; index++) {
			Address adrVx = createAddressVersion(adrV1, index);
			em.persist(adrVx);
		}

		for (int index = 0; index < quantity; index++) {
			em.persist(createRandomEmployee(quantity));
		}
	}

	public void createAddressVersions(EntityManager em) {
		em.getTransaction().begin();
		Address adrV1 = createAddress();
		em.persist(adrV1);
		em.getTransaction().commit();
		em.clear();
		
		sleep1();

		queryAllAddress(em);

		Address _adrV1 = getCurrent(em);

		em.getTransaction().begin();
		_adrV1.setCity("MUC");
		em.merge(adrV1);
		em.getTransaction().commit();
		em.clear();
		
		sleep1();
		
		
//		Employee emp1 = createRandomEmployee(_adrV1);
//		em.getTransaction().begin();
//		em.persist(emp1);
//		em.getTransaction().commit();
//		em.clear();
//
//		sleep1();
//
//		queryAllAddress(em);
//
//		em.getTransaction().begin();
//		_adrV1.setValidFrom(new Date());
//		em.persist(_adrV1);
//		em.getTransaction().commit();
//		em.clear();
//
//		sleep1();
//
//		queryAllAddress(em);
//
//		// need to create an insert
//		//
//		_adrV1 = em.find(Address.class, new Address.ID(adrV1.getId(), adrV1.getValidFrom()));
//		em.detach(_adrV1);
//		em.getTransaction().begin();
//		_adrV1.setStreet("Blablalba");
//		em.merge(_adrV1);
//		em.getTransaction().commit();
//		em.clear();
//
//		sleep1();
//
//		_adrV1 = em.find(Address.class, new Address.ID(_adrV1.getId(), _adrV1.getValidFrom()));
//
//		// Employee emp1 = createRandomEmployee(_adrV1);
//		// em.getTransaction().begin();
//		// em.persist(emp1);
//		// em.getTransaction().commit();
//		// em.clear();
//
//		sleep1();
//
//		// // update an old version
//		// Address oldVersion = queryFristAddress(em);
//		// em.detach(oldVersion);
//		// oldVersion.setStreet("Foobar");
//		// em.getTransaction().begin();
//		// em.merge(oldVersion);
//		// em.getTransaction().commit();
//		// em.clear();
//
//		emp1 = em.find(Employee.class, emp1.getId());
//		em.detach(emp1);
//		emp1.setFirstName("Foo bar foo");
//		emp1.getAddress().setStreet("HD");
//		em.getTransaction().begin();
//		em.merge(emp1);
//		em.getTransaction().commit();
//		em.clear();
//
//		sleep1();
//
//		emp1 = em.find(Employee.class, emp1.getId());
//		em.detach(emp1);
//		emp1.setFirstName("Foo bar foo");
//		emp1.getAddress().setStreet("HD2");
//		em.getTransaction().begin();
//		em.merge(emp1);
//		em.getTransaction().commit();
//		em.clear();
//
//		sleep1();
//
//		emp1 = em.find(Employee.class, emp1.getId());
//		em.detach(emp1);
//		emp1.setFirstName("Foo bar foo");
//		emp1.getAddress().setStreet("HD3");
//		em.getTransaction().begin();
//		em.merge(emp1);
//		em.getTransaction().commit();
//		em.clear();
//
//		emp1 = em.find(Employee.class, emp1.getId());
//		System.out.println(emp1.getAddress().getCity());
//
//		sleep1();
//
//		Address addrV3 = queryActiveAddress(em);
//		addrV3.setCity("NEW YORK");
//		em.getTransaction().begin();
//		em.merge(addrV3);
//		em.getTransaction().commit();
//		em.clear();
//
//		sleep1();
//
//		emp1 = em.find(Employee.class, emp1.getId());
//		System.out.println(emp1.getAddress().getCity());
//
//		sleep1();
	}

	private void sleep() {
		// TODO Auto-generated method stub

	}

	private void sleep1() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}

	private Address getCurrent(EntityManager em) {
		for(Address adr : queryAllAddress(em)) {
			if(adr.isHistCurrent()) {
				return adr;
			}
		}
		return null;
	}
	
	public List<Address> queryAllAddress(EntityManager em) {
		List<Address> results = em.createQuery("SELECT e FROM Address e", Address.class).getResultList();

		System.out.println("Query All Results: " + results.size());

		for (Address adr : results) {
			System.out.println("\t>" + adr.getId() + " AValidFrom: "
					+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(adr.getValidFrom()) + " AValidFUntil: "
					+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(adr.getValidUntil()) + " Version: "
					+ adr.getVersion() + " active: " + adr.isHistCurrent());
		}
		
		return results;
	}

	public Address queryFristAddress(EntityManager em) {
		List<Address> results = em.createQuery("SELECT e FROM Address e", Address.class).getResultList();

		return results.get(0);
	}

	public Address queryActiveAddress(EntityManager em) {
		List<Address> results = em.createQuery("SELECT e FROM Address e where e.version = 1", Address.class)
				.getResultList();

		return results.get(0);
	}

	private static final String[] MALE_FIRST_NAMES = { "Jacob", "Ethan", "Michael", "Alexander", "William", "Joshua",
			"Daniel", "Jayden", "Noah", "Anthony" };
	private static final String[] FEMALE_FIRST_NAMES = { "Isabella", "Emma", "Olivia", "Sophia", "Ava", "Emily",
			"Madison", "Abigail", "Chloe", "Mia" };
	private static final String[] LAST_NAMES = { "Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis",
			"Garcia", "Rodriguez", "Wilson" };

	List<Address> addresses = new ArrayList<>();

	public Employee createRandomEmployee(int quantity) {
		Random r = new Random();

		Employee emp = new Employee();
		emp.setGender(Gender.values()[r.nextInt(2)]);
		if (Gender.Male.equals(emp.getGender())) {
			emp.setFirstName(MALE_FIRST_NAMES[r.nextInt(MALE_FIRST_NAMES.length)]);
		} else {
			emp.setFirstName(FEMALE_FIRST_NAMES[r.nextInt(FEMALE_FIRST_NAMES.length)]);
		}
		emp.setLastName(LAST_NAMES[r.nextInt(LAST_NAMES.length)]);

		emp.setAddress(addresses.get(r.nextInt(quantity - 1)));

		return emp;
	}

	public Employee createRandomEmployee(Address adr) {
		Random r = new Random();

		Employee emp = new Employee();
		emp.setGender(Gender.values()[r.nextInt(2)]);
		if (Gender.Male.equals(emp.getGender())) {
			emp.setFirstName(MALE_FIRST_NAMES[r.nextInt(MALE_FIRST_NAMES.length)]);
		} else {
			emp.setFirstName(FEMALE_FIRST_NAMES[r.nextInt(FEMALE_FIRST_NAMES.length)]);
		}
		emp.setLastName(LAST_NAMES[r.nextInt(LAST_NAMES.length)]);

		emp.setAddress(adr);

		return emp;
	}

	public Address createAddress() {
		Address address = new Address("VIENNA", "AT", "W", "1010", "irgendwo");
		address.setId(UUID.randomUUID().toString());
		addresses.add(address);
		return address;
	}

	public Address createAddressVersion(Address address, int version) {
		try {
			// ensure valid from differs
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		Address newVersion = address.newVersion();
		newVersion.setStreet(address.getStreet() + " : " + version);
		addresses.add(newVersion);
		return newVersion;
	}
}
