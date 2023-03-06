package customer.business_partner_validation.clients.em;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EMData {
	
	@JsonProperty("BusinessPartner")
	private String businessPartner;

	public String getBusinessPartner() {
		return businessPartner;
	}

	public void setBusinessPartner(String businessPartner) {
		this.businessPartner = businessPartner;
	}
}