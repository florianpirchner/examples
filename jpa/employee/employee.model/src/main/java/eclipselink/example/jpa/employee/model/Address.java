/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 		dclarke - initial JPA Employee example using XML (bug 217884)
 *      mbraeuer - annotated version
 ******************************************************************************/
package eclipselink.example.jpa.employee.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.QueryRedirectors;

@Entity
@Cache(refreshOnlyIfNewer = true)
@IdClass(Address.ID.class)
@QueryRedirectors(insert = AddressInsertQueryRedirector.class, update = AddressInsertQueryRedirector.class)
public class Address extends BaseUUID {

	@Id
	@Column(name = "ADR_VALIDFROM", updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date validFrom;

	@Temporal(TemporalType.TIMESTAMP)
	private Date validUntil;

	@Basic
	private boolean histCurrent;

	@Basic
	private String city;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	private String country;

	@Basic
	private String province;

	@Basic
	@Column(name = "P_CODE")
	private String postalCode;

	@Basic
	private String street;

	public Address() {
	}

	public Address(String city, String country, String province, String postalCode, String street) {
		super();
		this.city = city;
		this.country = country;
		this.province = province;
		this.postalCode = postalCode;
		this.street = street;
		this.validFrom = new Date();
	}

	// @PrePersist
	// @PreUpdate
	// void postPersist() {
	// validFrom = new Date();
	// }

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(Date validUntil) {
		this.validUntil = validUntil;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getPostalCode() {
		return this.postalCode;
	}

	public void setPostalCode(String pCode) {
		this.postalCode = pCode;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public boolean isHistCurrent() {
		return histCurrent;
	}

	public void setHistCurrent(boolean histCurrent) {
		this.histCurrent = histCurrent;
	}

	public Address newVersion() {
		Address newVersion = new Address();
		newVersion.setId(getId());
		newVersion.validFrom = new Date();
		newVersion.city = city;
		newVersion.country = city;
		newVersion.postalCode = city;
		newVersion.province = city;
		newVersion.street = city;
		return newVersion;
	}

	// public void newVersionInternal() {
	// this.validFrom = new Date();
	// }

	@Override
	public String toString() {
		return "Address [validFrom=" + validFrom + ", validUntil=" + validUntil + ", histCurrent=" + histCurrent
				+ ", getId()=" + getId() + ", getVersion()=" + getVersion() + "]";
	}

	public static class ID implements Serializable {
		private static final long serialVersionUID = 1L;

		public String id;
		public Date validFrom;

		public ID() {
		}

		public ID(String empId, Date validFrom) {
			this.id = empId;
			this.validFrom = validFrom;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ID other = (ID) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (validFrom == null) {
				if (other.validFrom != null)
					return false;
			} else if (!validFrom.equals(other.validFrom))
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((validFrom == null) ? 0 : validFrom.hashCode());
			return result;
		}
	}
}
