/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package ec.edu.utmachala.titulacion.controller;

import ec.edu.utmachala.titulacion.entity.Asignatura;
import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.DocenteAsignatura;
import ec.edu.utmachala.titulacion.entity.EstudianteBiblioteca;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.MallaCurricular;
import ec.edu.utmachala.titulacion.entity.Parametro;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.UnidadCurricular;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.entityAux.CarreraMallaProcesoAgrupado;
import ec.edu.utmachala.titulacion.service.AsignaturaService;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.DocenteAsignaturaService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.MallaCurricularService;
import ec.edu.utmachala.titulacion.service.ParametroService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsMail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.Transport;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.primefaces.context.RequestContext;
import org.springframework.web.util.WebUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpServletRequest.class, 
    FacesContext.class,
    HttpServletResponse.class, 
    EstudianteBean.class, 
    UtilsAplicacion.class, 
    WebUtils.class,
    HttpSession.class,
    Transport.class})
public class AdministrarAsignaturaBeanTest {
    
    @InjectMocks
    private AdministrarAsignaturaBean administrarAsignaturaBeanController = new AdministrarAsignaturaBean();
    
    @Mock
    private AsignaturaService asignaturaService;
    
    @Mock
    private Asignatura asignatura;
    
    @Mock
    private UnidadCurricular unidadCurricular;
    
    @Mock
    private CarreraMallaProcesoAgrupado carreraMallaProcesoAgrupado;
    
    @Mock
    private UsuarioService usuarioService;
    
    @Mock
    private EstudiantesExamenComplexivoPPService estudiantesExamenComplexivoPPService;
    
    @Mock
    private DocenteAsignaturaService docenteAsignaturaService;
    
    @Mock
    private DocenteAsignatura docenteAsignatura;
   
    @Mock
    private RequestContext requestContext;
	
    @Mock
    private FacesContext facesContext;
    
    @Mock
    private Usuario docente;
    
    @Mock
    private CarreraService carreraService;
    
    @Mock
    private ProcesoService procesoService;
    
    @Mock
    private ParametroService parametroService;
    
    @Mock
    private UtilsMail utilsMail;
    
    @Mock
    private MallaCurricularService mallaCurricularService;
    
    @Before
    public void setUp() {
        // mock all static methods of FacesContext using PowerMockito
        //MockitoAnnotations.initMocks(this);
    	PowerMockito.mockStatic(FacesContext.class);
	RequestContext.setCurrentInstance(requestContext, facesContext);
    }
    
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void testActualizar() {
        ArgumentCaptor<String> clientIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
	    	// Mock the FacesMessage objects
		List<FacesMessage> expectedMessages = new ArrayList<>();
		expectedMessages.add(new FacesMessage(FacesMessage.SEVERITY_INFO, "MENSAJE DEL SISTEMA", "Actualizó correctamente la asignatura"));
				
		// Set up the mock FacesContext to return the expected messages
		when(facesContext.getMessageList()).thenReturn(expectedMessages);
		when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
        
        // Set up test data
        when(asignatura.getNombre()).thenReturn("Contabilidad");
        when(unidadCurricular.getId()).thenReturn("1");
        when(asignatura.getUnidadCurricular()).thenReturn(unidadCurricular);
        when(carreraMallaProcesoAgrupado.getMalla()).thenReturn(2);
       
        administrarAsignaturaBeanController.actualizar();
        
        // Verify that the methods were called
        verify(asignatura).setNombre("CONTABILIDAD");
        verify(asignaturaService).actualizar(asignatura);
        
        verify(facesContext).addMessage(clientIdCaptor.capture(),facesMessageCaptor.capture());
	    	
        // check the value of the clientId that was passed
        assertNull(clientIdCaptor.getValue());

        // retrieve the captured FacesMessage
        FacesMessage captured = facesMessageCaptor.getValue();

        assertEquals(expectedMessages.get(0).getSeverity(), captured.getSeverity());
        assertEquals(expectedMessages.get(0).getSummary(), captured.getSummary());
        assertEquals(expectedMessages.get(0).getDetail(), captured.getDetail());
    }
    
    @Test
    public void testActualizarWithEmptyNombre() {
        ArgumentCaptor<String> clientIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
	    // Mock the FacesMessage objects
		List<FacesMessage> expectedMessages = new ArrayList<>();
		expectedMessages.add(new FacesMessage(FacesMessage.SEVERITY_INFO, "MENSAJE DEL SISTEMA", "Ingrese un nombre"));
				
		// Set up the mock FacesContext to return the expected messages
		when(facesContext.getMessageList()).thenReturn(expectedMessages);
		when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
        
        // Set up test data
        when(asignatura.getNombre()).thenReturn("");
        when(unidadCurricular.getId()).thenReturn("1");
        when(asignatura.getUnidadCurricular()).thenReturn(unidadCurricular);
        //when(carreraMallaProcesoAgrupado.getMalla()).thenReturn(2);
       
        administrarAsignaturaBeanController.actualizar();
               
        verify(facesContext).addMessage(clientIdCaptor.capture(),facesMessageCaptor.capture());
        
        // check the value of the clientId that was passed
        assertNull(clientIdCaptor.getValue());

        // retrieve the captured FacesMessage
        FacesMessage captured = facesMessageCaptor.getValue();

        assertEquals(expectedMessages.get(0).getSeverity(), captured.getSeverity());
        assertEquals(expectedMessages.get(0).getSummary(), captured.getSummary());
        assertEquals(expectedMessages.get(0).getDetail(), captured.getDetail());
    }
    @Test
    public void testActualizarWithEmptyUnidadCurricular() {
        ArgumentCaptor<String> clientIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
	    	// Mock the FacesMessage objects
		List<FacesMessage> expectedMessages = new ArrayList<>();
		expectedMessages.add(new FacesMessage(FacesMessage.SEVERITY_INFO, "MENSAJE DEL SISTEMA", "Ingrese una unidad curricular"));
				
		// Set up the mock FacesContext to return the expected messages
		when(facesContext.getMessageList()).thenReturn(expectedMessages);
		when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
        
        // Set up test data
        when(asignatura.getNombre()).thenReturn("CONTABILIDAD");
        when(unidadCurricular.getId()).thenReturn("");
        when(asignatura.getUnidadCurricular()).thenReturn(unidadCurricular);
       
        administrarAsignaturaBeanController.actualizar();
               
        verify(facesContext).addMessage(clientIdCaptor.capture(),facesMessageCaptor.capture());
        
        // check the value of the clientId that was passed
        assertNull(clientIdCaptor.getValue());

        // retrieve the captured FacesMessage
        FacesMessage captured = facesMessageCaptor.getValue();

        assertEquals(expectedMessages.get(0).getSeverity(), captured.getSeverity());
        assertEquals(expectedMessages.get(0).getSummary(), captured.getSummary());
        assertEquals(expectedMessages.get(0).getDetail(), captured.getDetail());
    }
    
    @Test
    public void testBuscarDocentecriterioBusquedaDocenteEmpty() {

		ArgumentCaptor<String> clientIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
		List<Usuario> listadoDocentes = new ArrayList<Usuario>();
		String criterioBusquedaDocente = "";
		
        List<FacesMessage> expectedMessages = new ArrayList<>();
        expectedMessages.add(new FacesMessage(FacesMessage.SEVERITY_INFO, "MENSAJE DEL SISTEMA", "No deje el campo vacío para buscar el docente."));
			
        when(facesContext.getMessageList()).thenReturn(expectedMessages);        
        
        administrarAsignaturaBeanController.setCriterioBusquedaDocente(criterioBusquedaDocente);
        administrarAsignaturaBeanController.setListadoDocentes(listadoDocentes);
		
		when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
		 
		administrarAsignaturaBeanController.buscarDocente();
	    
	    verify(facesContext).addMessage(clientIdCaptor.capture(),facesMessageCaptor.capture());
		
        assertNull(clientIdCaptor.getValue());
	
        FacesMessage captured = facesMessageCaptor.getValue();
	    
        assertEquals(expectedMessages.get(0).getSeverity(), captured.getSeverity());
        assertEquals(expectedMessages.get(0).getSummary(), captured.getSummary());
        assertEquals(expectedMessages.get(0).getDetail(), captured.getDetail());
    }
    
    @Test
    public void testBuscarDocentecriterioBusquedaDocenteLength() {

		ArgumentCaptor<String> clientIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
		List<Usuario> listadoDocentes = new ArrayList<Usuario>();
		String criterioBusquedaDocente = "Jo";
		
        List<FacesMessage> expectedMessages = new ArrayList<>();
        expectedMessages.add(new FacesMessage(FacesMessage.SEVERITY_INFO, "MENSAJE DEL SISTEMA", "Ingrese más de dos letras para buscar al docente."));
			
        when(facesContext.getMessageList()).thenReturn(expectedMessages);
        
        administrarAsignaturaBeanController.setCriterioBusquedaDocente(criterioBusquedaDocente);
        administrarAsignaturaBeanController.setListadoDocentes(listadoDocentes);
		
		when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
		 
		administrarAsignaturaBeanController.buscarDocente();
	    
	    verify(facesContext).addMessage(clientIdCaptor.capture(),facesMessageCaptor.capture());
		
        assertNull(clientIdCaptor.getValue());
	
        FacesMessage captured = facesMessageCaptor.getValue();
	    
        assertEquals(expectedMessages.get(0).getSeverity(), captured.getSeverity());
        assertEquals(expectedMessages.get(0).getSummary(), captured.getSummary());
        assertEquals(expectedMessages.get(0).getDetail(), captured.getDetail());
    }
    
    @Test
    public void testBuscarEstudiante() {

    	List<Usuario> listadoDocentes = new ArrayList<Usuario>();
    	String criterioBusquedaDocente = "JORGE";
	
    	administrarAsignaturaBeanController.setCriterioBusquedaDocente(criterioBusquedaDocente);
    	administrarAsignaturaBeanController.buscarDocente();
                
    	Usuario docente = new Usuario();
    	docente.setApellidoNombre("JORGE BENITES");
    	docente.setEmail("mail@mail.com");
    	listadoDocentes.add(docente);
    	
        when(usuarioService.obtenerLista("select distinct u from Usuario u inner join u.permisos p inner join p.rol r "
				+ "where (u.id like ?1 or translate(lower(u.apellidoNombre), 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')"
				+ " like translate(lower(?1), 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) and "
				+ "(r.id='DOEV' or  r.id='DORE' or r.id='DOTU' or r.id='DOTE') order by u.apellidoNombre",
		new Object[] { "%" + criterioBusquedaDocente + "%" }, 0, false, new Object[] {})).thenReturn(listadoDocentes);

        assertEquals(1,listadoDocentes.size());
        assertEquals("JORGE BENITES", listadoDocentes.get(0).getApellidoNombre());
        assertEquals("mail@mail.com", listadoDocentes.get(0).getEmail());
    }
    
    @Test
    public void testeliminarAsignaturaTrue()
    {
    	ArgumentCaptor<String> clientIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
		List<FacesMessage> expectedMessages = new ArrayList<>();
        expectedMessages.add(new FacesMessage(FacesMessage.SEVERITY_INFO, "MENSAJE DEL SISTEMA", "Se activo correctamente la asignatura"));
			
        when(facesContext.getMessageList()).thenReturn(expectedMessages);
        
    	when(asignatura.getActivo()).thenReturn(true);
    	when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
    	
    	//asignaturaService.actualizar(asignatura);
    	administrarAsignaturaBeanController.eliminar();
    	
    	verify(facesContext).addMessage(clientIdCaptor.capture(),facesMessageCaptor.capture());
		
        assertNull(clientIdCaptor.getValue());
	
        FacesMessage captured = facesMessageCaptor.getValue();
	    
        assertEquals(expectedMessages.get(0).getSeverity(), captured.getSeverity());
        assertEquals(expectedMessages.get(0).getSummary(), captured.getSummary());
        assertEquals(expectedMessages.get(0).getDetail(), captured.getDetail());
    }
    @Test
    public void testeliminarAsignaturaFalse()
    {
    	ArgumentCaptor<String> clientIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
		List<FacesMessage> expectedMessages = new ArrayList<>();
        expectedMessages.add(new FacesMessage(FacesMessage.SEVERITY_INFO, "MENSAJE DEL SISTEMA", "Se desactivo correctamente la asignatura"));
			
        when(facesContext.getMessageList()).thenReturn(expectedMessages);
        
    	when(asignatura.getActivo()).thenReturn(false);
    	when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
    	
    	administrarAsignaturaBeanController.eliminar();
    	
    	verify(facesContext).addMessage(clientIdCaptor.capture(),facesMessageCaptor.capture());
		
        assertNull(clientIdCaptor.getValue());
	
        FacesMessage captured = facesMessageCaptor.getValue();
	    
        assertEquals(expectedMessages.get(0).getSeverity(), captured.getSeverity());
        assertEquals(expectedMessages.get(0).getSummary(), captured.getSummary());
        assertEquals(expectedMessages.get(0).getDetail(), captured.getDetail());
    }
    
    @Test
    public void testeliminarDocenteListadoEPP()
    {
    	ArgumentCaptor<String> clientIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
		List<FacesMessage> expectedMessages = new ArrayList<>();
		List<EstudianteExamenComplexivoPP> listadoEPP = new ArrayList<EstudianteExamenComplexivoPP>();
		listadoEPP.add(new EstudianteExamenComplexivoPP());
	
        expectedMessages.add(new FacesMessage(FacesMessage.SEVERITY_INFO, "MENSAJE DEL SISTEMA", "No puede eliminar el docente porque ha realizado reactivos, en tal caso debe desactivarlo."));
			
        when(facesContext.getMessageList()).thenReturn(expectedMessages);      
    	when(docenteAsignatura.getId()).thenReturn(1);
    	
    	when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
    	when(estudiantesExamenComplexivoPPService.obtenerLista(
				"select epp from EstudianteExamenComplexivoPP epp inner join epp.docenteAsignatura da where da.id=?1",
				new Object[] { docenteAsignatura.getId() }, 0, false, new Object[] {})).thenReturn(listadoEPP);
    	
    	administrarAsignaturaBeanController.eliminarDocente();
    	
    	verify(facesContext).addMessage(clientIdCaptor.capture(),facesMessageCaptor.capture());
		
        assertNull(clientIdCaptor.getValue());
	
        FacesMessage captured = facesMessageCaptor.getValue();
	    
        assertEquals(expectedMessages.get(0).getSeverity(), captured.getSeverity());
        assertEquals(expectedMessages.get(0).getSummary(), captured.getSummary());
        assertEquals(expectedMessages.get(0).getDetail(), captured.getDetail());
    }
    @Test
    public void testeliminarDocenteSinListadoEPP()
    {
    	ArgumentCaptor<String> clientIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
		List<FacesMessage> expectedMessages = new ArrayList<>();
		List<EstudianteExamenComplexivoPP> listadoEPP = new ArrayList<EstudianteExamenComplexivoPP>();
	
        expectedMessages.add(new FacesMessage(FacesMessage.SEVERITY_INFO, "MENSAJE DEL SISTEMA", "Docente eliminado de la asignatura satisfactoriamente."));
			
        when(facesContext.getMessageList()).thenReturn(expectedMessages);      
    	when(docenteAsignatura.getId()).thenReturn(1);
    	
    	when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
    	when(estudiantesExamenComplexivoPPService.obtenerLista(
				"select epp from EstudianteExamenComplexivoPP epp inner join epp.docenteAsignatura da where da.id=?1",
				new Object[] { docenteAsignatura.getId() }, 0, false, new Object[] {})).thenReturn(listadoEPP);
    	
    	administrarAsignaturaBeanController.eliminarDocente();
    	
    	verify(facesContext).addMessage(clientIdCaptor.capture(),facesMessageCaptor.capture());
		
        assertNull(clientIdCaptor.getValue());
	
        FacesMessage captured = facesMessageCaptor.getValue();
	    
        assertEquals(expectedMessages.get(0).getSeverity(), captured.getSeverity());
        assertEquals(expectedMessages.get(0).getSummary(), captured.getSummary());
        assertEquals(expectedMessages.get(0).getDetail(), captured.getDetail());
    }
    
    @Test
    public void testguardarDocenteEmpty()
    {
    	ArgumentCaptor<String> clientIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
		List<FacesMessage> expectedMessages = new ArrayList<>();
		
		expectedMessages.add(new FacesMessage(FacesMessage.SEVERITY_INFO, "MENSAJE DEL SISTEMA", "No ha seleccionado un docente."));
		
	    when(facesContext.getMessageList()).thenReturn(expectedMessages);
	    when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
	    when(docente.getId()).thenReturn("");
	    
	    administrarAsignaturaBeanController.guardarDocente();
	    
	    verify(facesContext).addMessage(clientIdCaptor.capture(),facesMessageCaptor.capture());
		
        assertNull(clientIdCaptor.getValue());
	
        FacesMessage captured = facesMessageCaptor.getValue();
	    
        assertEquals(expectedMessages.get(0).getSeverity(), captured.getSeverity());
        assertEquals(expectedMessages.get(0).getSummary(), captured.getSummary());
        assertEquals(expectedMessages.get(0).getDetail(), captured.getDetail());
    	    	
    }
 
    @Test
    public void testInsertarAsignaturaEmpty()
    {
    	ArgumentCaptor<String> clientIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        List<FacesMessage> expectedMessages = new ArrayList<>();

        expectedMessages.add(new FacesMessage(FacesMessage.SEVERITY_INFO, "MENSAJE DEL SISTEMA", "Ingrese un nombre de la asignatura"));
		
    	Asignatura tAsignatura = new Asignatura();
    	tAsignatura.setNombre("");
        UnidadCurricular tUnidad = new UnidadCurricular();
        tUnidad.setId("1");
        tUnidad.setNombre("Test");
        tAsignatura.setUnidadCurricular(tUnidad);
    	MallaCurricular mc = new MallaCurricular();
        when(asignatura.getNombre()).thenReturn(tAsignatura.getNombre());
        when(asignatura.getUnidadCurricular()).thenReturn(tAsignatura.getUnidadCurricular());
        when(mallaCurricularService.obtenerObjeto(
					"select mc from MallaCurricular mc where mc.id=?1",
					new Object[] { carreraMallaProcesoAgrupado.getMalla() }, false, new Object[0])).thenReturn(mc);
        
    	when(facesContext.getMessageList()).thenReturn(expectedMessages);
	when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
	    
    	administrarAsignaturaBeanController.insertar();
    	
    	verify(facesContext).addMessage(clientIdCaptor.capture(),facesMessageCaptor.capture());
		
        assertNull(clientIdCaptor.getValue());
	
        FacesMessage captured = facesMessageCaptor.getValue();
	    
        assertEquals(expectedMessages.get(0).getSeverity(), captured.getSeverity());
        assertEquals(expectedMessages.get(0).getSummary(), captured.getSummary());
        assertEquals(expectedMessages.get(0).getDetail(), captured.getDetail());
    }

    @Test
    public void testInsertarUnidadCurricularEmpty()
    {
    	ArgumentCaptor<String> clientIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        List<FacesMessage> expectedMessages = new ArrayList<>();

        expectedMessages.add(new FacesMessage(FacesMessage.SEVERITY_INFO, "MENSAJE DEL SISTEMA", "Escoger la unidad curricular."));
		
    	Asignatura tAsignatura = new Asignatura();
    	tAsignatura.setNombre("CONTABILIDAD");
        UnidadCurricular tUnidad = new UnidadCurricular();
        tUnidad.setId("0");
        tAsignatura.setUnidadCurricular(tUnidad);
    	MallaCurricular mc = new MallaCurricular();
        when(asignatura.getNombre()).thenReturn(tAsignatura.getNombre());
        when(asignatura.getUnidadCurricular()).thenReturn(tAsignatura.getUnidadCurricular());
        when(mallaCurricularService.obtenerObjeto(
					"select mc from MallaCurricular mc where mc.id=?1",
					new Object[] { carreraMallaProcesoAgrupado.getMalla() }, false, new Object[0])).thenReturn(mc);
        
    	when(facesContext.getMessageList()).thenReturn(expectedMessages);
	when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
	    
    	administrarAsignaturaBeanController.insertar();
    	
    	verify(facesContext).addMessage(clientIdCaptor.capture(),facesMessageCaptor.capture());
		
        assertNull(clientIdCaptor.getValue());
	
        FacesMessage captured = facesMessageCaptor.getValue();
	    
        assertEquals(expectedMessages.get(0).getSeverity(), captured.getSeverity());
        assertEquals(expectedMessages.get(0).getSummary(), captured.getSummary());
        assertEquals(expectedMessages.get(0).getDetail(), captured.getDetail());
    }
    
    @Test
    public void testInsertarAsignatura()
    {
    	ArgumentCaptor<String> clientIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        List<FacesMessage> expectedMessages = new ArrayList<>();

        expectedMessages.add(new FacesMessage(FacesMessage.SEVERITY_INFO, "MENSAJE DEL SISTEMA", "Inserto la asignatura correctamente"));
		
    	Asignatura tAsignatura = new Asignatura();
    	tAsignatura.setNombre("CONTABILIDAD");
        UnidadCurricular tUnidad = new UnidadCurricular();
        tUnidad.setId("1");
        tAsignatura.setUnidadCurricular(tUnidad);
    	MallaCurricular mc = new MallaCurricular();
        when(asignatura.getNombre()).thenReturn(tAsignatura.getNombre());
        when(asignatura.getUnidadCurricular()).thenReturn(tAsignatura.getUnidadCurricular());
        when(mallaCurricularService.obtenerObjeto(
					"select mc from MallaCurricular mc where mc.id=?1",
					new Object[] { carreraMallaProcesoAgrupado.getMalla() }, false, new Object[0])).thenReturn(mc);
        
    	when(facesContext.getMessageList()).thenReturn(expectedMessages);
	when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
	    
    	administrarAsignaturaBeanController.insertar();
    	
    	verify(facesContext).addMessage(clientIdCaptor.capture(),facesMessageCaptor.capture());
		
        assertNull(clientIdCaptor.getValue());
	
        FacesMessage captured = facesMessageCaptor.getValue();
	    
        assertEquals(expectedMessages.get(0).getSeverity(), captured.getSeverity());
        assertEquals(expectedMessages.get(0).getSummary(), captured.getSummary());
        assertEquals(expectedMessages.get(0).getDetail(), captured.getDetail());
    }
    
}
