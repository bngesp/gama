package gaml.additions;
import msi.gama.outputs.layers.*;
import msi.gama.outputs.*;
import msi.gama.kernel.batch.*;
import msi.gaml.architecture.weighted_tasks.*;
import msi.gaml.architecture.user.*;
import msi.gaml.architecture.reflex.*;
import msi.gaml.architecture.finite_state_machine.*;
import msi.gaml.species.*;
import msi.gama.metamodel.shape.*;
import msi.gaml.expressions.*;
import msi.gama.metamodel.topology.*;
import msi.gama.metamodel.population.*;
import msi.gama.kernel.simulation.*;
import java.util.*;
import  msi.gama.metamodel.shape.*;
import msi.gama.common.interfaces.*;
import msi.gama.runtime.*;
import java.lang.*;
import msi.gama.metamodel.agent.*;
import msi.gaml.types.*;
import msi.gaml.compilation.*;
import msi.gaml.factories.*;
import msi.gaml.descriptions.*;
import msi.gama.util.file.*;
import msi.gama.util.matrix.*;
import msi.gama.util.graph.*;
import msi.gama.util.path.*;
import msi.gama.util.*;
import msi.gama.runtime.exceptions.*;
import msi.gaml.factories.*;
import msi.gaml.statements.*;
import msi.gaml.skills.*;
import msi.gaml.variables.*;
import msi.gama.kernel.experiment.*;
import msi.gaml.operators.*;
import msi.gaml.extensions.genstar.*;
import msi.gaml.operators.Random;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Points;
import msi.gaml.operators.Spatial.Properties;
import msi.gaml.operators.System;
import static msi.gaml.operators.Cast.*;
import static msi.gaml.operators.Spatial.*;
import static msi.gama.common.interfaces.IKeyword.*;

public class GamlAdditions extends AbstractGamlAdditions {
	public void initialize() throws SecurityException, NoSuchMethodException {
	initializeTypes();
	initializeSymbols();
	initializeVars();
	initializeOperators();
	initializeFiles();
	initializeActions();
	initializeSkills();
	initializeSpecies();
	initializeDisplays();
	initializePopulationsLinkers();
}
public void initializeTypes() {};
public void initializeSpecies() {};
public void initializeSymbols() {};
public void initializeVars() throws SecurityException, NoSuchMethodException {
_var(dream.gama.opengis.operators.SocketSkill.class,desc(4,S(TYPE,"4",NAME,"ip",CONST,FALSE)),new GamaHelper(dream.gama.opengis.operators.SocketSkill.class){ @Override public String run(IScope scope, IAgent a, ISkill t, Object... v) {return t == null? null:((dream.gama.opengis.operators.SocketSkill)t).getIP(a);}},null,new GamaHelper(dream.gama.opengis.operators.SocketSkill.class){ @Override public Object run(IScope scope, IAgent a, ISkill t, Object... arg) {if (t != null) ((dream.gama.opengis.operators.SocketSkill) t).setIP(a, (String) arg[0]); return null; }});
_var(dream.gama.opengis.operators.SocketSkill.class,desc(1,S(TYPE,"1",NAME,"port",CONST,FALSE)),new GamaHelper(dream.gama.opengis.operators.SocketSkill.class){ @Override public Integer run(IScope scope, IAgent a, ISkill t, Object... v) {return t == null? 0:((dream.gama.opengis.operators.SocketSkill)t).getPort(a);}},null,new GamaHelper(dream.gama.opengis.operators.SocketSkill.class){ @Override public Object run(IScope scope, IAgent a, ISkill t, Object... arg) {if (t != null) ((dream.gama.opengis.operators.SocketSkill) t).setPort(a, (Integer) arg[0]); return null; }});
_var(dream.gama.opengis.operators.SocketSkill.class,desc(4,S(TYPE,"4",NAME,"msg",CONST,FALSE)),new GamaHelper(dream.gama.opengis.operators.SocketSkill.class){ @Override public String run(IScope scope, IAgent a, ISkill t, Object... v) {return t == null? null:((dream.gama.opengis.operators.SocketSkill)t).getMessage(a);}},null,new GamaHelper(dream.gama.opengis.operators.SocketSkill.class){ @Override public Object run(IScope scope, IAgent a, ISkill t, Object... arg) {if (t != null) ((dream.gama.opengis.operators.SocketSkill) t).setMesage(a, (String) arg[0]); return null; }});};
public void initializeOperators() throws SecurityException, NoSuchMethodException  {
_operator(S("read_json_rest"),dream.gama.opengis.operators.OpenGIS.class.getMethod("read_json_rest", IScope.class,S,S),C(S,S),I(),LI,T,-13,-13,-13,new GamaHelper(){ @Override public IList run(IScope s,Object... o){return dream.gama.opengis.operators.OpenGIS.read_json_rest(s,((String)o[0]),((String)o[1]));}});
_operator(S("gml_from_wfs"),dream.gama.opengis.operators.OpenGIS.class.getMethod("read_wfs", IScope.class,S,S,S),C(S,S,S),I(),LI,T,-13,-13,-13,new GamaHelper(){ @Override public IList run(IScope s,Object... o){return dream.gama.opengis.operators.OpenGIS.read_wfs(s,((String)o[0]),((String)o[1]),((String)o[2]));}});
_operator(S("image_from_direct_wms"),dream.gama.opengis.operators.OpenGIS.class.getMethod("read_wms_direct", IScope.class,S,S),C(S,S),I(),GF,T,-13,-13,-13,new GamaHelper(){ @Override public IGamaFile run(IScope s,Object... o){return dream.gama.opengis.operators.OpenGIS.read_wms_direct(s,((String)o[0]),((String)o[1]));}});
_operator(S("image_from_wms"),dream.gama.opengis.operators.OpenGIS.class.getMethod("read_wms", IScope.class,S,S,I,I,I,D,D,D,D),C(S,S,I,I,I,D,D,D,D),I(),GF,T,-13,-13,-13,new GamaHelper(){ @Override public IGamaFile run(IScope s,Object... o){return dream.gama.opengis.operators.OpenGIS.read_wms(s,((String)o[0]),((String)o[1]),asInt(s,o[2]),asInt(s,o[3]),asInt(s,o[4]),asFloat(s,o[5]),asFloat(s,o[6]),asFloat(s,o[7]),asFloat(s,o[8]));}});};
public void initializeFiles() throws SecurityException, NoSuchMethodException  {};
public void initializeActions() {
_action("primSendToServer",dream.gama.opengis.operators.SocketSkill.class,new GamaHelper(T(void.class), dream.gama.opengis.operators.SocketSkill.class){ @Override public Object run(IScope s, IAgent a,ISkill t, Object... v){  ((dream.gama.opengis.operators.SocketSkill) t).primSendToServer(s); return null;} },desc(PRIMITIVE, null, new ChildrenProvider(Arrays.asList(desc(ARG,NAME,"msg", "optional", FALSE))), NAME, "send_to_server",TYPE, Ti(void.class), VIRTUAL,FALSE));
_action("primListenClient",dream.gama.opengis.operators.SocketSkill.class,new GamaHelper(T(void.class), dream.gama.opengis.operators.SocketSkill.class){ @Override public Object run(IScope s, IAgent a,ISkill t, Object... v){  ((dream.gama.opengis.operators.SocketSkill) t).primListenClient(s); return null;} },desc(PRIMITIVE, null, new ChildrenProvider(Arrays.asList()), NAME, "listen_client",TYPE, Ti(void.class), VIRTUAL,FALSE));
_action("primListenServer",dream.gama.opengis.operators.SocketSkill.class,new GamaHelper(T(void.class), dream.gama.opengis.operators.SocketSkill.class){ @Override public Object run(IScope s, IAgent a,ISkill t, Object... v){  ((dream.gama.opengis.operators.SocketSkill) t).primListenServer(s); return null;} },desc(PRIMITIVE, null, new ChildrenProvider(Arrays.asList()), NAME, "listen_server",TYPE, Ti(void.class), VIRTUAL,FALSE));
_action("primOpenSocket",dream.gama.opengis.operators.SocketSkill.class,new GamaHelper(T(void.class), dream.gama.opengis.operators.SocketSkill.class){ @Override public Object run(IScope s, IAgent a,ISkill t, Object... v){  ((dream.gama.opengis.operators.SocketSkill) t).primOpenSocket(s); return null;} },desc(PRIMITIVE, null, new ChildrenProvider(Arrays.asList()), NAME, "open_socket",TYPE, Ti(void.class), VIRTUAL,FALSE));
_action("primSendToClient",dream.gama.opengis.operators.SocketSkill.class,new GamaHelper(T(void.class), dream.gama.opengis.operators.SocketSkill.class){ @Override public Object run(IScope s, IAgent a,ISkill t, Object... v){  ((dream.gama.opengis.operators.SocketSkill) t).primSendToClient(s); return null;} },desc(PRIMITIVE, null, new ChildrenProvider(Arrays.asList(desc(ARG,NAME,"msg", "optional", FALSE))), NAME, "send_to_client",TYPE, Ti(void.class), VIRTUAL,FALSE));};
public void initializeSkills() {
_skill("socket",dream.gama.opengis.operators.SocketSkill.class, new ISkillConstructor(){ @Override public ISkill newInstance(){return new dream.gama.opengis.operators.SocketSkill();}});};
public void initializeDisplays() {};
public void initializePopulationsLinkers() {};

}