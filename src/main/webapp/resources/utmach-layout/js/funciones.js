PrimeFaces.locales['es'] = {
	closeText : 'Cerrar',
	prevText : 'Anterior',
	nextText : 'Siguiente',
	monthNames : [ 'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
			'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre',
			'Diciembre' ],
	monthNamesShort : [ 'Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago',
			'Sep', 'Oct', 'Nov', 'Dic' ],
	dayNames : [ 'Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves',
			'Viernes', 'Sábado' ],
	dayNamesShort : [ 'Dom', 'Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab' ],
	dayNamesMin : [ 'D', 'L', 'M', 'X', 'J', 'V', 'S' ],
	weekHeader : 'Semana',
	firstDay : 1,
	isRTL : false,
	showMonthAfterYear : false,
	yearSuffix : '',
	timeOnlyTitle : 'Sólo hora',
	timeText : 'Tiempo',
	hourText : 'Hora',
	minuteText : 'Minuto',
	secondText : 'Segundo',
	currentText : 'Fecha actual',
	ampm : false,
	month : 'Mes',
	week : 'Semana',
	day : 'Día',
	allDayText : 'Todo el día',
	yes : 'Si',
	no : 'No',
	Yes : 'Si',
	No : 'No'
};

var $col, $lbl, a;
function focusBtn() {
	$('#formRespuesta\\:btns tr').each(function() {
		$col = $(this).find('td').children();
		for (var i = 0; i < $col.length; i++) {
			$btn = $col.children([ 0 ]);
			a = $col.parent()[1];
			a = a.getElementsByTagName('label')
			$lbl = $col.parents().find(a);
			if ($btn.hasClass('ui-state-active')) {
				$lbl.addClass('labelFocus');
			} else {
				$lbl.removeClass('labelFocus');
			}
		}
	});

	return false;
}

var $col1, $lbl1, a1;
function focusBtn1(form) {
	$('#' + form + '\\:btns tr').each(function() {
		$col1 = $(this).find('td').children();
		for (var i = 0; i < $col1.length; i++) {
			$btn1 = $col1.children([ 0 ]);
			a1 = $col1.parent()[1];
			a1 = a1.getElementsByTagName('label')
			$lbl1 = $col1.parents().find(a1);
			if ($btn1.hasClass('ui-state-active')) {
				$lbl1.addClass('labelFocus');
			} else {
				$lbl1.removeClass('labelFocus');
			}
		}
	});

	return false;
}

function mostarPanel(panel) {
	$panelPrincipal = $('#panelPrincipal');
	$panelInsertar = $('#' + panel);
	$panelPrincipal.slideToggle();
	$panelInsertar.slideToggle();
	return false;
}

function mostarPanel1(panel1, panel2) {
	$panelPrincipal = $('#' + panel1);
	$panelInsertar = $('#' + panel2);
	$panelPrincipal.slideToggle();
	$panelInsertar.slideToggle();
	return false;
}

function mostarPanelBusqueda(id) {
	$panelBusqueda = $('#' + id);
	$panelBusqueda.slideToggle();
	return false;
}

function comprobarAbrir(xhr, status, args, id) {
	if (!args.validationFailed && args.abrir) {
		PF(id).show();
	}
}

function comprobarCerrar(xhr, status, args, id) {
	if (!args.validationFailed && args.cerrar) {
		PF(id).hide();
	}
}

function comprobarInsertar(xhr, status, args, id) {
	if (!args.validationFailed && args.cerrar) {
		mostarPanel(id);
	}
}

function comprobar1(xhr, status, args, panel1, panel2) {
	if (!args.validationFailed && args.cerrar) {
		mostarPanel1(panel1, panel2);
	}
}

function comprobar(xhr, status, args, id) {
	if (!args.validationFailed && args.cerrar) {
		mostarPanel(id);
	}
}

function mostrarIcono(id) {
	$('#' + id).removeClass("OcultarIcono");
}

function ocultarIcono(id) {
	$('#' + id).addClass("OcultarIcono");
}

function click(id) {
	$('#' + id).click();
}

var minterval;
window.onload = function() {

	var $btnTimer = $('#formTimer\\:btnTimer')
	$btnTimer.click();
	actualizarEnunciado();
	actualizarPRA();
	actualizarPRB();
	actualizarPRC();
	actualizarPRD();

	// /////////////////////
	PF('dlg').show();
}
function obtenerTiempo() {
	var $cronometro = $('#formTimer\\:cronometro');
	$timer = $('#formTimer\\:timer');
	var tiempo = $timer.text();
	console.log(tiempo)
	$cronometro.text(tiempo);
	minterval = setInterval(function() {
		tiempo--;
		if (tiempo < 0) {
			tiempo = 0;
		}
		$cronometro.text(tiempo);
		if ($cronometro.text() == '0') {
			console.log('entre')
			$btn1 = $('#formTimer\\:updPreRep');
			$btn1.click();
			clearInterval(minterval);
		}
	}, 1000);
}
function successPregunta(xhr, status, args) {
	if (args.vistaCalificacion != false) {
		var $btnTimer = $('#formTimer\\:btnTimer')
		$btnTimer.click();
		console.log('success');
	}
}

function actualizarEnunciado() {
	var $enunciadoVP = $('#formNueva\\:editor').val();
	$enunciadoVP = $enunciadoVP.replace(/\n\r?/g, "<br/>");
	$enunciadoVP = $enunciadoVP
			.replace(/<tab>/g,
					"<span class=\"Apple-tab-span\" style=\"white-space:pre\">	</span>");
	$('#VP\\:enunciadoVP').html($enunciadoVP);
}

function actualizarPRA() {
	var $editorA = $('#formP2\\:editorA').val();
	$editorA = $editorA.replace(/\n\r?/g, "<br/>");
	$editorA = $editorA
			.replace(/<tab>/g,
					"<span class=\"Apple-tab-span\" style=\"white-space:pre\">	</span>");
	$('#VP\\:opA').html($editorA);
}

function actualizarPRB() {
	var $editorB = $('#formP2\\:editorB').val();
	$editorB = $editorB.replace(/\n\r?/g, "<br/>");
	$editorB = $editorB
			.replace(/<tab>/g,
					"<span class=\"Apple-tab-span\" style=\"white-space:pre\">	</span>");
	$('#VP\\:opB').html($editorB);
}

function actualizarPRC() {
	var $editorC = $('#formP2\\:editorC').val();
	$editorC = $editorC.replace(/\n\r?/g, "<br/>");
	$editorC = $editorC
			.replace(/<tab>/g,
					"<span class=\"Apple-tab-span\" style=\"white-space:pre\">	</span>");
	$('#VP\\:opC').html($editorC);
}

function actualizarPRD() {
	var $editorD = $('#formP2\\:editorD').val();
	$editorD = $editorD.replace(/\n\r?/g, "<br/>");
	$editorD = $editorD
			.replace(/<tab>/g,
					"<span class=\"Apple-tab-span\" style=\"white-space:pre\">	</span>");
	$('#VP\\:opD').html($editorD);
}

function validarHojaEntregaRecepcion(estado) {
	if (estado == null) {
		PF('WVmensaje').renderMessage({
			"summary" : "summary goes here",
			"detail" : "detail goes here",
			"severity" : "info"
		});
	}
}
function validarDocumentoTT() {
	var $txtInconsistencia = $("#formValidacionDocumento\\:txtInconsistencia")
			.val();
	var $cbAprobado = $(
			"#formValidacionDocumento\\:sbbVerificaciónArchivo_input").attr(
			'checked');
	console.log("Valor de la caja de texto cheked o no " + $cbAprobado);
	if ($cbAprobado) {
		PrimeFaces.bcn(this, event, [ function(event) {
			PrimeFaces.ab({
				s : "formValidacionDocumento:btnGuardarValidarDocumento",
				u : "panelPrincipal panelTrabajoEscrito mensaje"
			});
			return false;
		} ]);
	} else {
		if ($txtInconsistencia == "" || $txtInconsistencia == " "
				|| $txtInconsistencia == null) {
			alert("Debe ingresar alguna inconsistencia encontrada para enviar el correo electrónico al estudiante y realice las respectivas correcciones."
					+ $txtInconsistencia);
		} else {
			PrimeFaces.bcn(this, event, [ function(event) {
				PrimeFaces.ab({
					s : "formValidacionDocumento:btnGuardarValidarDocumento",
					u : "panelPrincipal panelTrabajoEscrito mensaje"
				});
				return false;
			} ]);
		}
	}
}
function validarDocumentoEC() {
	var $txtInconsistencia = $("#formValidacionDocumento\\:txtInconsistencia")
			.val();
	var $cbAprobado = $(
			"#formValidacionDocumento\\:sbbVerificaciónArchivo_input").attr(
			'checked');
	console.log("Valor de la caja de texto cheked o no " + $cbAprobado);
	if ($cbAprobado) {
		PrimeFaces.ab({
			s : "formValidacionDocumento:btnGuardarValidarDocumento",
			u : "panelPrincipal panelTrabajoEscrito mensaje"
		});
		return false;
	} else {
		if ($txtInconsistencia == "" || $txtInconsistencia == " "
				|| $txtInconsistencia == null) {
			alert("Debe ingresar alguna inconsistencia encontrada para enviar el correo electrónico al estudiante y realice las respectivas correcciones."
					+ $txtInconsistencia);
		} else {
			PrimeFaces.ab({
				s : "formValidacionDocumento:btnGuardarValidarDocumento",
				u : "panelPrincipal panelTrabajoEscrito mensaje"
			});
			return false;
		}
	}
}
function validarTxtUrlBiblioteca() {
	var $txtUrlBiblioteca = $("#formValidacionDocumento\\:txtUrlBiblioteca")
			.val();
	if ($txtUrlBiblioteca.length <= 36) {
		alert('Debe ingresar una ruta completa');
	} else if ($txtUrlBiblioteca == null || $txtUrlBiblioteca == ""
			|| $txtUrlBiblioteca == " ") {
		alert('Si quiere guardar, no puede dejar el campo ruta de DSPACE vacío');
	} else {
		PrimeFaces.ab({
			s : "formValidacionDocumento:btnGuardarValidarDocumento",
			u : "panelPrincipal panelTrabajoEscrito mensaje"
		});
	}
}
function enunciadoKeyPress() {
	var $txtEnunciado = $("#formNueva\\:editor");
	$txtEnunciadoPre = $("#formPregunta\\:lblEnunciado");
	if ($txtEnunciado.val() != '') {
		$txtEnunciadoPre
				.html($txtEnunciado
						.val()
						.replace(/\n\r?/g, "<br/>")
						.replace(/<tab>/g,
								"<span class=\"Apple-tab-span\" style=\"white-space:pre\">	</span>"));
	} else {
		$txtEnunciadoPre.html("El enunciado o planteamiento va aquí.");
	}
}
function akeyPress() {
	var $opcionA = $("#formEditorA\\:editorR1").val();
	if ($opcionA != '') {
		$('label[for=formRespuesta\\:btns\\:0]').html('A.- ' + $opcionA);
	} else {
		$('label[for=formRespuesta\\:btns\\:0]').html('A.- ');
	}
	// var $radio = $("#formRespuesta\\:btns\\:0").val();
}
function bkeyPress() {
	var $opcionB = $("#formEditorB\\:editorR2").val();
	if ($opcionB != '') {
		$('label[for=formRespuesta\\:btns\\:1]').html('B.- ' + $opcionB);
	} else {
		$('label[for=formRespuesta\\:btns\\:1]').html('B.- ');
	}
	// var $radio = $("#formRespuesta\\:btns\\:1").val();
}
function ckeyPress() {
	var $opcionC = $("#formEditorC\\:editorR3").val();
	if ($opcionC != '') {
		$('label[for=formRespuesta\\:btns\\:2]').html('C.- ' + $opcionC);
	} else {
		$('label[for=formRespuesta\\:btns\\:2]').html('C.- ');
	}
	// var $radio = $("#formRespuesta\\:btns\\:2").val();
}
function dkeyPress() {
	var $opcionD = $("#formEditorD\\:editorR4").val();
	if ($opcionD != '') {
		$('label[for=formRespuesta\\:btns\\:3]').html('D.- ' + $opcionD);
	} else {
		$('label[for=formRespuesta\\:btns\\:3]').html('D.- ');
	}
	// var $radio = $("#formRespuesta\\:btns\\:0").val();
}
function formatearTituloInvestigacion() {
	var $titulo = $("#formTitulo\\:tituloInvestigacion").val();
	var $prev = $("#formTitulo\\:opTituloInvestigacion");
	$prev.html($titulo);
}
function llamarModalBibliotecaBusqueda() {
	$checkBusqueda = $("#busqueda\\:checkBusqueda_input").prop("checked");
	if ($checkBusqueda) {
		PF('ventanaBusqueda').show();
	}
}
function llamarModalCertificadoECBusqueda() {
	$checkBusqueda = $(
			"#tabViewBusqueda\\:busqueda\\:checkBusquedaCertificadosEC_input")
			.prop("checked");
	if ($checkBusqueda) {
		PF('ventanaBusquedaCertificadoEC').show();
	}
}
function llamarModalCertificadoTTBusqueda() {
	$checkBusqueda = $(
			"#tabViewBusqueda\\:busqueda\\:checkBusquedaCertificadosTT_input")
			.prop("checked");
	if ($checkBusqueda) {
		PF('ventanaBusquedaCertificadoTT').show();
	}
}
function llamarModalEstudianteBusqueda() {
	$checkBusqueda = $("#formBusquedaEstudiante\\:checkBusqueda_input").prop(
			"checked");
	if ($checkBusqueda) {
		PF('ventanaBusquedaGeneral').show();
	}
}
function cambiarEstadoTextAreaCertificados(btn) {
	$btnAdeuda = $(btn);
	console.log('El index es: ' + $btnAdeuda.index());
	alert("El texto a mostrar es:" + $btnAdeuda.is(':checked'));
}
function validarGuardarAdeuda(btn) {
	$btnAdeuda = $(btn);
	$id = $btnAdeuda.attr('id');
	$numeroID = $id.split(":")[3];
	console.log("El id es: " + $numeroID);
	$checkAdeuda = $("#tabViewBusqueda\\:formularioTablaCertificado\\:tablaCertificado\\:"
			+ $numeroID + "\\:adeuda-_input");
	$txtDescripcionAdeuda = $("#tabViewBusqueda\\:formularioTablaCertificado\\:tablaCertificado\\:"
			+ $numeroID + "\\:txtDescripcionAdeuda");
	if ($checkAdeuda.prop("checked")) {
		if ($txtDescripcionAdeuda.val().length <= 10) {
			alert("Por favor, debe añadir texto de mínimo 10 caracteres o más.");
		} else {
			PrimeFaces
					.ab({
						s : "tabViewBusqueda:formularioTablaCertificado:tablaCertificado:"
								+ $numeroID + ":btnGuardar-",
						u : "tabViewBusqueda:formularioTablaCertificado mensaje"
					});
			return false;
		}
	} else {

		PrimeFaces.ab({
			s : "tabViewBusqueda:formularioTablaCertificado:tablaCertificado:"
					+ $numeroID + ":btnGuardar-",
			u : "tabViewBusqueda:formularioTablaCertificado mensaje"
		});
		return false;
	}
}

function validarGuardarDevolvio(btn) {
	$btnDevolvio = $(btn);
	$id = $btnDevolvio.attr('id');
	$numeroID = $id.split(":")[3];
	console.log("El id es: " + $numeroID);
	$checkDevolvio = $("#tabViewBusqueda\\:formularioTablaCertificado\\:tablaCertificado\\:"
			+ $numeroID + "\\:devolvio-_input");
	$txtDescripcionDevolvio = $("#tabViewBusqueda\\:formularioTablaCertificado\\:tablaCertificado\\:"
			+ $numeroID + "\\:txtDescripcionDevolvio");
	$checkAdeuda = $("#tabViewBusqueda\\:formularioTablaCertificado\\:tablaCertificado\\:"
			+ $numeroID + "\\:adeuda-_input");
	console.log("valor del check adeuda: " + $checkAdeuda);
	if ($checkDevolvio.prop("checked")) {
		if ($checkAdeuda.prop("checked")) {
			if ($txtDescripcionDevolvio.val().length <= 10) {
				alert("Por favor, debe añadir texto de mínimo 10 caracteres o más.");
			} else {
				PrimeFaces
						.ab({
							s : "tabViewBusqueda:formularioTablaCertificado:tablaCertificado:"
									+ $numeroID + ":btnGuardarDevolvio-",
							u : "tabViewBusqueda:formularioTablaCertificado mensaje"
						});
				return false;
			}
		} else {
			if ($txtDescripcionDevolvio.val().length > 1) {
				alert("No se puede guardar.");
			} else {
				PrimeFaces
						.ab({
							s : "tabViewBusqueda:formularioTablaCertificado:tablaCertificado:"
									+ $numeroID + ":btnGuardarDevolvio-",
							u : "tabViewBusqueda:formularioTablaCertificado mensaje"
						});
				return false;
			}
		}
	} else {
		if ($checkAdeuda.prop("checked")) {
			alert("No se puede guardar ya que el estudiante tiene una deuda pendiente.");
		} else {
			if ($txtDescripcionDevolvio.val().length > 1) {
				alert("No se puede guardar.");
			} else {
				PrimeFaces
						.ab({
							s : "tabViewBusqueda:formularioTablaCertificado:tablaCertificado:"
									+ $numeroID + ":btnGuardarDevolvio-",
							u : "tabViewBusqueda:formularioTablaCertificado mensaje"
						});
				return false;
			}
		}
	}
}

function validarIngresoInformacion() {
	$txtNumeroOficioUMMOGTesoreria = $(
			"#formEstudiantesAprobadosTesoreria\\:txtEstudiantesAprobadosTesoreria")
			.val();
	$txtFechaActaGraduacion = $(
			"#formEstudiantesAprobadosTesoreria\\:txtFechaGraduacion_input")
			.val();
	if ($txtNumeroOficioUMMOGTesoreria == "" || $txtFechaActaGraduacion == "") {
		alert("Recuerde indicar una fecha de graduación y número de oficio.");
	} else {
		PF('dlgEstudiantesTesoreriaConfirmar').show();
		pasarOficioUMMOGTesoreria();
		pasarFechaGraduacion();
	}
}

function validarIngresoInformacionSecretaria() {
	$txtNumeroOficioUMMOGSecretaria = $(
			"#formEstudiantesAprobadosSecretaria\\:txtEstudiantesAprobadosSecretaria")
			.val();
	$txtfechaOficioUMMOGSecretaria = $(
			"#formEstudiantesAprobadosSecretaria\\:txtfechaOficioUMMOGSecretaria_input")
			.val();
	if ($txtNumeroOficioUMMOGSecretaria == ""
			|| $txtfechaOficioUMMOGSecretaria == "") {
		alert("Recuerde indicar una fecha y número de oficio.");
	} else {
		PF('dlgEstudiantesSecretariaConfirmar').show();
		pasarOficioUMMOGSecretaria();
		pasarfechaOficioUMMOGSecretaria();
		pasarInformacionHomologacionesSecretaria();
	}
}

function pasarOficioUMMOGTesoreria() {
	$txtNumeroOficioUMMOGTesoreria = $(
			"#formEstudiantesAprobadosTesoreria\\:txtEstudiantesAprobadosTesoreria")
			.val();
	console.log("El valor del primer txt: " + $txtNumeroOficioUMMOGTesoreria);
	$txtNumeroOficioUMMOGTesoreriaConfirmar = $("#formEstudiantesAprobadosTesoreriaConfirmar\\:txtEstudiantesAprobadosTesoreriaConfirmar");
	$txtNumeroOficioUMMOGTesoreriaConfirmar.val($txtNumeroOficioUMMOGTesoreria);
	console.log("El valor del segundo txt: "
			+ $txtNumeroOficioUMMOGTesoreriaConfirmar.val());
}

function pasarOficioUMMOGSecretaria() {
	$txtNumeroOficioUMMOGSecretaria = $(
			"#formEstudiantesAprobadosSecretaria\\:txtEstudiantesAprobadosSecretaria")
			.val();
	console.log("El valor del primer txt: " + $txtNumeroOficioUMMOGSecretaria);
	$txtNumeroOficioUMMOGSecretariaConfirmar = $("#formEstudiantesAprobadosSecretariaConfirmar\\:txtEstudiantesAprobadosSecretariaConfirmar");
	$txtNumeroOficioUMMOGSecretariaConfirmar
			.val($txtNumeroOficioUMMOGSecretaria);
	console.log("El valor del segundo txt: "
			+ $txtNumeroOficioUMMOGSecretariaConfirmar.val());
}

function pasarFechaGraduacion() {
	$txtFechaActaGraduacion = $(
			"#formEstudiantesAprobadosTesoreria\\:txtFechaGraduacion_input")
			.val();
	console.log("El valor del calendario txt: " + $txtFechaActaGraduacion);
	$txtFechaActaGraduacionConfirmar = $("#formEstudiantesAprobadosTesoreriaConfirmar\\:txtFechaGraduacionConfirmar_input");
	$txtFechaActaGraduacionConfirmar.val($txtFechaActaGraduacion);
	console.log("El valor del segundo txt: "
			+ $txtFechaActaGraduacionConfirmar.val());
}

function pasarfechaOficioUMMOGSecretaria() {
	$txtfechaOficioUMMOGSecretaria = $(
			"#formEstudiantesAprobadosSecretaria\\:txtfechaOficioUMMOGSecretaria_input")
			.val();
	console.log("El valor del calendario txt: "
			+ $txtfechaOficioUMMOGSecretaria);
	$txtfechaOficioUMMOGSecretariaConfirmar = $("#formEstudiantesAprobadosSecretariaConfirmar\\:txtfechaOficioUMMOGSecretariaConfirmar_input");
	$txtfechaOficioUMMOGSecretariaConfirmar.val($txtfechaOficioUMMOGSecretaria);
	console.log("El valor del segundo txt: "
			+ $txtfechaOficioUMMOGSecretariaConfirmar.val());
}
function pasarInformacionHomologacionesSecretaria() {
	$chkHomologaciones = $("#formEstudiantesAprobadosSecretaria\\:chkHomologaciones_input");
	console.log("El valor del checked es : "
			+ $chkHomologaciones.prop('checked'));
	$chkHomologacionesConfirmar = $("#formEstudiantesAprobadosSecretariaConfirmar\\:chkHomologacionesConfirmacion_input");
	$chkHomologacionesConfirmar.prop("checked", $chkHomologaciones
			.prop('checked'));
	console.log("El valor del segundo del checked: "
			+ $chkHomologacionesConfirmar.prop('checked'));
}