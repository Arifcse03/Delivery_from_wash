import model.services.AppModuleImpl;
import java.util.Map;
import oracle.jbo.Row;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import oracle.jbo.ViewObject;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.nav.RichCommandButton;
import oracle.adf.view.rich.context.AdfFacesContext;
import javax.faces.event.ActionEvent;
import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ApplicationModule;
import oracle.jbo.ViewObject;

public class main {
    private RichTable linetable;
    private RichCommandButton customSaveButton2;
    private RichInputText totalissueqty;

    public main() {
    }
    public ApplicationModule getAppM() {
        DCBindingContainer bindingContainer =
            (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
        //BindingContext bindingContext = BindingContext.getCurrent();
        DCDataControl dc =
            bindingContainer.findDataControl("AppModuleDataControl"); // Name of application module in datacontrolBinding.cpx
        AppModuleImpl appM = (AppModuleImpl)dc.getDataProvider();
        return appM;
    }
    AppModuleImpl am = (AppModuleImpl)this.getAppM();

    public void popupdialogselect(DialogEvent dialogEvent) {
        // Add event code here...
        
        System.out.println("inside popup dialog selection");
               if (dialogEvent.getOutcome().name().equals("ok")) {
                   ViewObject populatevo = am.getViewObj1();
                   // populatevo.executeQuery();

                   Row[] r = populatevo.getAllRowsInRange();
                   System.out.println("Drop ------->" + r.length);
                   for (Row row : r) {

                       if (row.getAttribute("flag") != null &&
                           row.getAttribute("flag").equals("y")) {
                           System.out.println("flag --->" + row.getAttribute("flag"));
                           popSizeAll(row);
                       }
                   }
                   AdfFacesContext.getCurrentInstance().addPartialTarget(linetable);
               } else {
                   System.out.println("no selected");
               }
    }    
               
        public void popSizeAll(Row poprow) {
            //// here fill value is taking form fill Size popup And inserting below table

            Row linerow = getRollLine();

            linerow.setAttribute("SystemId", getPopulateValue(poprow, "SystemId"));
            linerow.setAttribute("TotProductionQty",
                                 getPopulateValue(poprow, "ProdQty"));


        } 
        
        
    
    public String getPopulateValue(Row r, String columnName) {

        String value = null;
        try {
            value = r.getAttribute(columnName).toString();
        } catch (Exception e) {
            ;
        }
        return value;
    }

    public Row getRollLine() {

        ViewObject vo = am.getXxOdmIssueToStitchLTVO2();
        Row row = vo.createRow();
        vo.insertRow(row);
        row.setNewRowState(Row.STATUS_INITIALIZED);
        return row;
    } //end of createHeader

    public void setLinetable(RichTable linetable) {
        this.linetable = linetable;
    }

    public RichTable getLinetable() {
        return linetable;
    }

    public void popupFetch(PopupFetchEvent popupFetchEvent) {
        // Add event code here...
        ViewObject vo = am.getViewObj1();
        vo.executeQuery();
    }

    public void CustomSave(ActionEvent actionEvent) {
        // Add event code here...
        // am.getDBTransaction().commit();
        ViewObject vo = am.getXxOdmIssueToStitchLTVO2();
        ViewObject hbo = am.getXxOdmIssueToStitchHTVO1();
        Row[] r =vo.getAllRowsInRange();
        String total_issue_qty = "0";
        String washsendqty = "0";
        double total = 0.00;

        for (Row row : r) {
            try {
                total =
                        total + (Double.parseDouble(row.getAttribute("StitchSendQty").toString()));

            } catch (Exception e) {
                showMessage("Please fill the Empty  Stitch Send Qty field and try again",
                            "error");

            }

        }

        System.out.println("after calculation total:"+total); 
        try {
            hbo.getCurrentRow().setAttribute("TotIssueQty", total);
        } catch (Exception e) {
           e.printStackTrace();
          System.out.println("total quantity set e error:"+total);  
        }

        try {
            System.out.println(hbo.getCurrentRow().getAttribute("UnitName").toString());

        } catch (Exception e) {
           

        }
        am.getDBTransaction().commit();
        vo.executeQuery();
        
        AdfFacesContext.getCurrentInstance().addPartialTarget(totalissueqty);
        

        
        
    }
    
    
    public void setFlagField(ActionEvent actionEvent) {
        // Add event code here...
        Map sessionScope = ADFContext.getCurrent().getSessionScope();

        ViewObject vo = am.getXxOdmIssueToStitchHTVO1();
        String resid = (String)sessionScope.get("respId");
        String value = null;
        String two = "2";
        String three = "3";
        String resone = "51850"; 
        String restwo = "51971";//wash
        String resthree = "51830";
        String resfour = "51654";
        try {
            value = vo.getCurrentRow().getAttribute("Flag").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (resid.equals(resone) ||resid.equals(resthree)|| resid.equals(resfour) && value.equals("1")) {

            vo.getCurrentRow().setAttribute("Flag", two);
        }
       
        else if(resid.equals(restwo) && value.equals("2")) {
            vo.getCurrentRow().setAttribute("Flag", three);
        }

        am.getDBTransaction().commit();
        System.out.println(resid);
        // AdfFacesContext.getCurrentInstance().addPartialTarget(customSaveButton);
        AdfFacesContext.getCurrentInstance().addPartialTarget(customSaveButton2);

    }

    public void setCustomSaveButton2(RichCommandButton customSaveButton2) {
        this.customSaveButton2 = customSaveButton2;
    }

    public RichCommandButton getCustomSaveButton2() {
        return customSaveButton2;
    }
    
    
    public void showMessage(String message, String severity) {

        FacesMessage fm = new FacesMessage(message);

        if (severity.equals("info")) {
            fm.setSeverity(FacesMessage.SEVERITY_INFO);
            System.out.println("inside message");
        } else if (severity.equals("warn")) {
            fm.setSeverity(FacesMessage.SEVERITY_WARN);
        } else if (severity.equals("error")) {
            fm.setSeverity(FacesMessage.SEVERITY_ERROR);
        }

        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, fm);

    }


    public void setTotalissueqty(RichInputText totalissueqty) {
        this.totalissueqty = totalissueqty;
    }

    public RichInputText getTotalissueqty() {
        return totalissueqty;
    }
}//main
