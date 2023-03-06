package customer.business_partner_validation.clients.em;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EMPayload {

	@JsonProperty("type")
	private String type;

	@JsonProperty("specversion")
	private String specversion;

	@JsonProperty("source")
	private String source;

	@JsonProperty("id")
	private String id;

	@JsonProperty("time")
	private String time;

	@JsonProperty("datacontenttype")
	private String datacontenttype;

	@JsonProperty("data")
	private EMData data;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSpecversion() {
		return specversion;
	}

	public void setSpecversion(String specversion) {
		this.specversion = specversion;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDatacontenttype() {
		return datacontenttype;
	}

	public void setDatacontenttype(String datacontenttype) {
		this.datacontenttype = datacontenttype;
	}

	public EMData getData() {
		return data;
	}

	public void setData(EMData data) {
		this.data = data;
	}

    @Override
    public String toString() {
        return "EMPayload [data=" + data + ", datacontenttype=" + datacontenttype + ", id=" + id + ", source=" + source
                + ", specversion=" + specversion + ", time=" + time + ", type=" + type + "]";
    }
}