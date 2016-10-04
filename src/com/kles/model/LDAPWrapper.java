package com.kles.model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ldaps")
public class LDAPWrapper {

    private List<IDataModelManager> datas;

    @XmlElement(name = "ldap")
    public List<IDataModelManager> getData() {
        return this.datas;
    }

    public void setData(List<IDataModelManager> environments) {
        this.datas = environments;
    }
}
