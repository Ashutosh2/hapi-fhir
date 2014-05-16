package ca.uhn.fhir.jpa.entity;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.primitive.IdDt;

@Entity
@Table(name = "BASE_RES", uniqueConstraints = {})
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "SVCVER_TYPE", length = 20, discriminatorType = DiscriminatorType.STRING)
public abstract class BaseResourceTable<T extends IResource> extends BaseHasResource {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "RES_ID")
	private Long myId;

	@OneToMany(mappedBy = "myResource", cascade = {}, fetch = FetchType.LAZY, orphanRemoval = false)
	private Collection<ResourceIndexedSearchParamDate> myParamsDate;

	@Column(name = "SP_DATE_PRESENT")
	private boolean myParamsDatePopulated;

	@OneToMany(mappedBy = "myResource", cascade = {}, fetch = FetchType.LAZY, orphanRemoval = false)
	private Collection<ResourceIndexedSearchParamString> myParamsString;

	@Column(name = "SP_STRING_PRESENT")
	private boolean myParamsStringPopulated;

	@OneToMany(mappedBy = "myResource", cascade = {}, fetch = FetchType.LAZY, orphanRemoval = false)
	private Collection<ResourceIndexedSearchParamToken> myParamsToken;

	@Column(name = "SP_TOKEN_PRESENT")
	private boolean myParamsTokenPopulated;

	@OneToMany(mappedBy = "myResource", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Collection<ResourceTag> myTags;

	@Version()
	@Column(name = "RES_VER")
	private Long myVersion;

	public void addTag(String theTerm, String theLabel, String theScheme) {
		for (ResourceTag next : getTags()) {
			if (next.getTerm().equals(theTerm)) {
				return;
			}
		}
		getTags().add(new ResourceTag(this, theTerm, theLabel, theScheme));
	}

	public Long getIdAsLong() {
		return myId;
	}

	public IdDt getId() {
		return new IdDt(myId);
	}

	public Collection<ResourceIndexedSearchParamDate> getParamsDate() {
		if (myParamsDate == null) {
			myParamsDate = new ArrayList<>();
		}
		return myParamsDate;
	}

	public Collection<ResourceIndexedSearchParamString> getParamsString() {
		if (myParamsString == null) {
			myParamsString = new ArrayList<>();
		}
		return myParamsString;
	}

	public Collection<ResourceIndexedSearchParamToken> getParamsToken() {
		if (myParamsToken == null) {
			myParamsToken = new ArrayList<>();
		}
		return myParamsToken;
	}

	public abstract Class<T> getResourceType();

	public Collection<ResourceTag> getTags() {
		if (myTags == null) {
			myTags = new ArrayList<>();
		}
		return myTags;
	}

	public IdDt getVersion() {
		return new IdDt(myVersion);
	}

	public boolean isParamsDatePopulated() {
		return myParamsDatePopulated;
	}

	public boolean isParamsStringPopulated() {
		return myParamsStringPopulated;
	}

	public boolean isParamsTokenPopulated() {
		return myParamsTokenPopulated;
	}

	public void setId(IdDt theId) {
		myId = theId.asLong();
	}

	public void setParamsDate(Collection<ResourceIndexedSearchParamDate> theParamsDate) {
		myParamsDate = theParamsDate;
	}

	public void setParamsDatePopulated(boolean theParamsDatePopulated) {
		myParamsDatePopulated = theParamsDatePopulated;
	}

	public void setParamsString(Collection<ResourceIndexedSearchParamString> theParamsString) {
		myParamsString = theParamsString;
	}

	public void setParamsStringPopulated(boolean theParamsStringPopulated) {
		myParamsStringPopulated = theParamsStringPopulated;
	}

	public void setParamsToken(Collection<ResourceIndexedSearchParamToken> theParamsToken) {
		myParamsToken = theParamsToken;
	}

	public void setParamsTokenPopulated(boolean theParamsTokenPopulated) {
		myParamsTokenPopulated = theParamsTokenPopulated;
	}

	public void setVersion(IdDt theVersion) {
		myVersion = theVersion.asLong();
	}

	public ResourceHistoryTable toHistory(FhirContext theCtx) {
		ResourceHistoryTable retVal = new ResourceHistoryTable();

		ResourceHistoryTablePk pk = new ResourceHistoryTablePk();
		pk.setId(myId);
		pk.setResourceType(theCtx.getResourceDefinition(getResourceType()).getName());
		pk.setVersion(myVersion);
		retVal.setPk(pk);

		retVal.setPublished(getPublished());
		retVal.setUpdated(getUpdated());
		retVal.setEncoding(getEncoding());
		retVal.setResource(getResource());

		for (ResourceTag next : getTags()) {
			retVal.addTag(next.getTerm(), next.getLabel(), next.getScheme());
		}

		return retVal;
	}
}