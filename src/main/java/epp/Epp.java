package epp;

import org.seamcat.model.Scenario;
import org.seamcat.model.factory.Factory;
import org.seamcat.model.plugin.eventprocessing.EventProcessingPlugin;
import org.seamcat.model.plugin.eventprocessing.PostProcessingTab;
import org.seamcat.model.plugin.eventprocessing.PostProcessingUI;
import org.seamcat.model.plugin.system.ConsistencyCheckContext;
import org.seamcat.model.simulation.consistency.Validator;
import org.seamcat.model.simulation.result.Collector;
import org.seamcat.model.simulation.result.EventResult;
import org.seamcat.model.simulation.result.LinkResult;
import org.seamcat.model.simulation.result.VectorDef;
import org.seamcat.model.types.Description;
import org.seamcat.model.types.InterferenceLink;
import org.seamcat.model.types.result.DescriptionImpl;

import java.util.ArrayList;
import java.util.List;



public class Epp implements EventProcessingPlugin<Epp.Input>,PostProcessingTab {

    public void evaluate(Scenario scenario, EventResult eventResult, Input input, Collector resultCollector )
    {
        for (InterferenceLink link : scenario.getInterferenceLinks()) {


            LinkResult victimResult = eventResult.getInterferenceLinkResult(link).get(0).getVictimSystemLink();

            resultCollector.add(Input.VLRX,victimResult.rxAntenna().getPosition().getX());
            resultCollector.add(Input.VLRY,victimResult.rxAntenna().getPosition().getY());
            resultCollector.add(Input.VLTX,victimResult.txAntenna().getPosition().getX());
            resultCollector.add(Input.VLTY,victimResult.txAntenna().getPosition().getY());


            // System.out.println("x="+victimResult.rxAntenna().getPosition().getX());
            // System.out.println("y="+victimResult.rxAntenna().getPosition().getY());


            for (int i = 0; i < eventResult.getInterferenceLinkResult(link).size(); i++) {
                LinkResult lr= eventResult.getInterferenceLinkResult(link).get(i).getInterferingSystemLink();

                //String iName = link.getInterferer().toString();
                resultCollector.add(Input.ILRX,lr.rxAntenna().getPosition().getX());
                resultCollector.add(Input.ILRY,lr.rxAntenna().getPosition().getY());
                resultCollector.add(Input.ILTX,lr.txAntenna().getPosition().getX());
                resultCollector.add(Input.ILTY,lr.txAntenna().getPosition().getY());

            }
        }

    }
    @Override
    public void consistencyCheck(ConsistencyCheckContext context, Input input, Validator validator){}
    public Description description(){
        return new DescriptionImpl("Manhattan Grid","grille de manhattan");
    }
    public interface Input{
        VectorDef VLRX = Factory.results().value("VLR position X","km");
        VectorDef VLRY = Factory.results().value("VLR position Y","km");
        VectorDef VLTX = Factory.results().value("VLT position X","km");
        VectorDef VLTY = Factory.results().value("VLT position Y","km");
        VectorDef ILRX = Factory.results().value("ILR position X","km");
        VectorDef ILRY = Factory.results().value("ILR position Y","km");
        VectorDef ILTX = Factory.results().value("ILT position X","km");
        VectorDef ILTY = Factory.results().value("ILT position Y","km");
    }
    @Override
    public List<Class<? extends PostProcessingUI>> tabs() {
        List<Class<? extends PostProcessingUI>> tabs = new ArrayList<>();
        tabs.add(EppUI.class);
        return tabs;
    }


}