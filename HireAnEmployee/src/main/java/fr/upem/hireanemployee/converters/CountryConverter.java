package fr.upem.hireanemployee.converters;

import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Logger;
import fr.upem.hireanemployee.profildata.Country;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Allows the conversion from a Country to a String, and in the other direction.
 */
@FacesConverter("countryConverter")
public class CountryConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        return Country.stringToCountry(s);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) { return o.toString(); }
}
