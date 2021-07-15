package com.bolsadeideas.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.service.IClienteService;
import com.bolsadeideas.springboot.app.service.IUploadFileService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	@Autowired
	private IClienteService clienteService;

	@Autowired
	private IUploadFileService uploadFileService;

//	@Value("${uploads.paths}")
//	private String rootPath;

	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {

		Resource recurso = null;
		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}

	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {
		String vista = "ver";

		Cliente cliente = clienteService.findById(id);
		if (cliente == null) {
			vista = "redirect:/listar";
			flash.addFlashAttribute("error", "El cliente no ha sido encontrado");
		} else {
			model.addAttribute("cliente", cliente);
			model.addAttribute("titulo", "Detalle cliente: " + cliente.getNombre());
		}
		return vista;
	}

	@RequestMapping(value = "listar", method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
		Pageable pageRequest = PageRequest.of(page, 5);

		Page<Cliente> clientes = clienteService.findAll(pageRequest);

		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);

		model.addAttribute("titulo", "Listado de clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);

		return "listar";
	}

	@GetMapping(value = "/form")
	public String crear(Map<String, Object> model) {
		Cliente cliente = new Cliente();
		model.put("cliente", cliente);
		model.put("titulo", "Formulario de cliente");
		model.put("btnValue", "Crear cliente");
		return "form";
	}

	@PostMapping(value = "/form")
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {

		String vista = "";
		String mensajeFlash = "";

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de cliente");
			vista = "form";
		} else {
			if (!foto.isEmpty()) {

				if (cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null
						&& cliente.getFoto().length() > 0) {

					uploadFileService.delete(cliente.getFoto());
				}

				String uniqueFilename = "";
				try {
					uniqueFilename = uploadFileService.copy(foto);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

//				byte[] bytes = foto.getBytes();
//					Path rutaCompleta = Paths.get(rootPath + "//" + foto.getOriginalFilename());

				flash.addFlashAttribute("info", "Has subido correctamente " + uniqueFilename);
				cliente.setFoto(uniqueFilename);
			}
			mensajeFlash = cliente.getId() != null ? "Cliente editado con exito" : "Cliente creado con exito";
			clienteService.save(cliente);
			status.setComplete();
			flash.addFlashAttribute("success", mensajeFlash);
			vista = "redirect:listar";
		}

		return vista;
	}

	@GetMapping(value = "/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		Cliente cliente = null;
		String vista = "";

		if (id > 0) {
			cliente = clienteService.findById(id);
			vista = "form";
			if (cliente == null) {
				flash.addFlashAttribute("error", "Cliente no encontrado");
				vista = "redirect:/listar";

			} else {
				model.put("cliente", cliente);
				model.put("btnValue", "Editar cliente");
				model.put("titulo", "Editar cliente");
			}

		} else {
			flash.addFlashAttribute("error", "Cliente no encontrado");
			vista = "redirect:/listar";
		}

		return vista;
	}

	@GetMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable Long id, RedirectAttributes flash) {

		if (id > 0) {
			Cliente cliente = clienteService.findById(id);

			clienteService.delete(id);
			flash.addFlashAttribute("success", "Cliente eliminado con exito");

			if (uploadFileService.delete(cliente.getFoto())) {
				flash.addFlashAttribute("info", "Foto " + cliente.getFoto() + " eliminada con exito");
			}
		}

		return "redirect:/listar";
	}

}
