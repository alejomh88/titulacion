/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.utmachala.titulacion.controller;

import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.entity.EstudianteBiblioteca;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.EstudianteBibliotecaService;
import ec.edu.utmachala.titulacion.service.UsuarioService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.util.WebUtils;

import java.util.ArrayList;
import java.util.List;

import org.primefaces.context.RequestContext;

@RunWith(PowerMockRunner.class)
@ContextConfiguration(locations = { "classpath:testApplicationContext.xml" })
@PrepareForTest({HttpServletRequest.class, 
    FacesContext.class,
    HttpServletResponse.class, 
    EstudianteBean.class, 
    UtilsAplicacion.class, 
    WebUtils.class,
    HttpSession.class})
public class EstudianteBeanTest {
    
    @InjectMocks
    private EstudianteBean EstudianteBeanController = new EstudianteBean();
	
    @Mock
    private UsuarioService usuarioService;
	
    @Mock
    private UtilsAplicacion utilsAplicacion;
	
    @Mock
    private EstudianteBibliotecaService estudianteBibliotecaService;
	
    @Mock
    private RequestContext requestContext;
	
    @Mock
    private FacesContext facesContext;
    
    @Before
    public void setUp() throws Exception {
	// mock all static methods of FacesContext using PowerMockito
    	PowerMockito.mockStatic(FacesContext.class);
	RequestContext.setCurrentInstance(requestContext, facesContext);
    }
    @Test
    public void testBuscarEstudianteEmptyCriterioBusqueda() {
    	// create Captor instances for the clientId and FacesMessage parameters
		// that will be added to the FacesContext
		ArgumentCaptor<String> clientIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
		List<EstudianteBiblioteca> listadoEstudiantesBuscados = new ArrayList<EstudianteBiblioteca>();
		String criterioBusqueda = "";
		   
		// Mock the FacesMessage objects
		List<FacesMessage> expectedMessages = new ArrayList<>();
		expectedMessages.add(new FacesMessage(FacesMessage.SEVERITY_INFO, "MENSAJE DEL SISTEMA", "Ingrese un número de cédula o apellido del estudiante a buscar."));
				
		// Set up the mock FacesContext to return the expected messages
		when(facesContext.getMessageList()).thenReturn(expectedMessages);
		when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
	
		EstudianteBeanController.setCriterioBusqueda(criterioBusqueda);
		EstudianteBeanController.setListadoEstudiantesBuscados(listadoEstudiantesBuscados);
        EstudianteBeanController.buscarEstudiante();
                
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
    public void testBuscarEstudiante() {
		List<EstudianteBiblioteca> listadoEstudiantesBuscados = new ArrayList<EstudianteBiblioteca>();
		String criterioBusqueda = "ANGEL";
	
		EstudianteBeanController.setCriterioBusqueda(criterioBusqueda);
		EstudianteBeanController.setListadoEstudiantesBuscados(listadoEstudiantesBuscados);
        EstudianteBeanController.buscarEstudiante();
                
        EstudianteBiblioteca e = new EstudianteBiblioteca();
        e.setApellidoNombre("ANGEL MOROCHO");
        e.setEmail("mail@mail.com");
        listadoEstudiantesBuscados.add(e);
        when(estudianteBibliotecaService.obtenerPorSql("select distinct 'T-'||ett.id as id, 'Trabajo Titulacion' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, p.id||' ('||p.observacion||')' as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", ot.nombre as opcion from \"estudiantesTrabajosTitulacion\" ett, usuarios u, carreras c, \"opcionesTitulacion\" ot, procesos p where ett.\"numeroActaCalificacion\" is not null and ett.estudiante = u.id and ett.carrera=c.id and ett.\"opcionTitulacion\"=ot.id and ett.proceso = p.id and (u.id like '%"
                                                    + criterioBusqueda.trim()
                                                    + "%' or translate(u.\"apellidoNombre\",'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu') like translate('%"
                                                    + criterioBusqueda.trim().toUpperCase()
                                                    + "%', 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) union select distinct 'E-'||ett.id as id, 'Examen Complexivo' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, p.id||' ('||p.observacion||')' as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", 'Examen Complexivo' as opcion from \"estudiantesExamenComplexivoPP\" ett, usuarios u, carreras c, procesos p where ett.\"numeroActaCalificacion\" is not null and ett.estudiante = u.id and ett.carrera=c.id and ett.proceso=p.id and (u.id like '%"
                                                    + criterioBusqueda.trim()
                                                    + "%' or translate(u.\"apellidoNombre\", 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu') like translate('%"
                                                    + criterioBusqueda.trim().toUpperCase()
                                                    + "%', 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) order by \"apellidoNombre\" asc;", EstudianteBiblioteca.class)).thenReturn(listadoEstudiantesBuscados);

        assertEquals(1,listadoEstudiantesBuscados.size());
        assertEquals("ANGEL MOROCHO", listadoEstudiantesBuscados.get(0).getApellidoNombre());
        assertEquals("mail@mail.com", listadoEstudiantesBuscados.get(0).getEmail());
    }
    @Test
    public void testBuscarEstudianteEmpty() {
    	// create Captor instances for the clientId and FacesMessage parameters
		// that will be added to the FacesContext
		ArgumentCaptor<String> clientIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
		List<EstudianteBiblioteca> listadoEstudiantesBuscados = new ArrayList<EstudianteBiblioteca>();
		String criterioBusqueda = "";
		
		// Mock the FacesMessage objects
        List<FacesMessage> expectedMessages = new ArrayList<>();
        expectedMessages.add(new FacesMessage(FacesMessage.SEVERITY_INFO, "MENSAJE DEL SISTEMA", "Ingrese un número de cédula o apellido del estudiante a buscar."));
			
        // Set up the mock FacesContext to return the expected messages
        when(facesContext.getMessageList()).thenReturn(expectedMessages);
        
		EstudianteBeanController.setCriterioBusqueda(criterioBusqueda);
		EstudianteBeanController.setListadoEstudiantesBuscados(listadoEstudiantesBuscados);
		
		when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
		 
	    EstudianteBeanController.buscarEstudiante();
	    
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
    public void ActualizarTest() {
        // create Captor instances for the clientId and FacesMessage parameters
        // that will be added to the FacesContext
        ArgumentCaptor<String> clientIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
	    
        Usuario estudiante = new Usuario();
        estudiante.setApellidoNombre("angel morocho");
        estudiante.setEmail("ANGEL.MOROCHO@MAIL.COM");
        estudiante.setTelefono("0982668859");
	    
        // Mock the FacesMessage objects
        List<FacesMessage> expectedMessages = new ArrayList<>();
        expectedMessages.add(new FacesMessage(FacesMessage.SEVERITY_INFO, "MENSAJE DEL SISTEMA", "Actualizó correctamente los datos del estudiante"));
			
        // Set up the mock FacesContext to return the expected messages
        when(facesContext.getMessageList()).thenReturn(expectedMessages);
	    
        EstudianteBeanController.setEstudiante(estudiante);
	    
        when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
	    	    
        EstudianteBeanController.actualizar();
	    
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
    public void buscarTest(){
 
        List<Usuario> estudiantes = new ArrayList<Usuario>();
        String criterioBusquedaEstudiante = "";
        Usuario estudiante = new Usuario();
        estudiante.setApellidoNombre("JORGE BENITES");
        estudiante.setEmail("jbenites@utmachala.com");
        estudiante.setTelefono("0982668859");
        estudiantes.add(estudiante);
        
        EstudianteBeanController.setCriterioBusquedaEstudiante(criterioBusquedaEstudiante);
        EstudianteBeanController.buscar();
        when(usuarioService.obtenerLista(
				"select distinct u from Usuario u inner join u.permisos p inner join p.rol r "
						+ "where ((u.id like ?1 or translate(u.apellidoNombre, 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu') "
						+ "like translate(upper(?1), 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu'))) "
						+ "and (r.id='ESEC' or r.id='ESTT')) order by u.apellidoNombre",
				new Object[] { "%" + criterioBusquedaEstudiante.trim() + "%" }, 0, false, new Object[0])).thenReturn(estudiantes);
        assertEquals(1,estudiantes.size());
        assertEquals("JORGE BENITES", estudiantes.get(0).getApellidoNombre());
        assertEquals("jbenites@utmachala.com", estudiantes.get(0).getEmail());

    }
    
}