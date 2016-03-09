package fr.upem.hireanemployee.beans;

import fr.upem.hireanemployee.DatabaseDAO;
import fr.upem.hireanemployee.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * Allows the conversion from a Skill to a String, and in the other direction.
 * We can not use @FacesConverter("skillConverter") Annotation, since we want to
 * access an EJB for database purposes.
 */
@ManagedBean
public class SkillConverter implements Converter {

    @EJB
    private DatabaseDAO dao;

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        Logger.log("[BEAN] Type of converted : " + s, Logger.BEAN);
        return  dao.getSkillByName(s);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        return o.toString();
    }
}
