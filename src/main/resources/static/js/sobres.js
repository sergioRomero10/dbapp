/* sobres.js: animación de apertura y revelación del personaje */
(function () {
	const form = document.getElementById('form-abrir');
	const sobre = document.getElementById('sobre');
	const revelacion = document.getElementById('revelacion');
	const reducido = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

	// 1. Al abrir: el sobre se agita y se rasga antes de enviar el formulario
	if (form) {
		form.addEventListener('submit', function (e) {
			if (form.dataset.enviando) return;
			e.preventDefault();
			form.dataset.enviando = 'true';
			document.querySelectorAll('button[form="form-abrir"], #form-abrir button')
				.forEach(b => b.disabled = true);

			if (revelacion) revelacion.classList.add('saliendo');
			sobre.classList.add('agitando');
			setTimeout(() => form.submit(), reducido ? 0 : 1100);
		});
	}

	if (!revelacion) return;

	// 2. Revelación: la carta aparece boca abajo con el aura de su rareza (suspense)
	//    y se gira al pulsarla o pasado un momento
	const carta = document.getElementById('carta');
	let girada = false;

	function girar() {
		if (girada) return;
		girada = true;
		carta.classList.add('girada');
		revelacion.classList.add('revelada');
		if (revelacion.classList.contains('rareza-legendaria')
				|| revelacion.classList.contains('rareza-epica')) {
			lanzarParticulas();
		}
	}

	function lanzarParticulas() {
		if (reducido) return;
		const contenedor = document.getElementById('particulas');
		for (let i = 0; i < 40; i++) {
			const p = document.createElement('span');
			const angulo = Math.random() * Math.PI * 2;
			const distancia = 160 + Math.random() * 260;
			p.style.setProperty('--x', Math.cos(angulo) * distancia + 'px');
			p.style.setProperty('--y', Math.sin(angulo) * distancia + 'px');
			p.style.animationDelay = Math.random() * 0.2 + 's';
			contenedor.appendChild(p);
		}
	}

	carta.addEventListener('click', girar);
	setTimeout(girar, reducido ? 0 : 2200);

	document.getElementById('btn-cerrar').addEventListener('click', function () {
		revelacion.classList.add('saliendo');
		setTimeout(() => revelacion.remove(), 300);
	});
})();
