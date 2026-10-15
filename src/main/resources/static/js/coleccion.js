/* coleccion.js: filtro del álbum por rareza y por cartas que faltan */
(function () {
	const botones = document.querySelectorAll('.filtro');
	const soloFaltan = document.getElementById('solo-faltan');
	const cartas = document.querySelectorAll('.carta-album');
	const vacio = document.getElementById('album-vacio');
	let rareza = 'todas';

	function aplicarFiltro() {
		let visibles = 0;
		cartas.forEach(carta => {
			const coincideRareza = rareza === 'todas' || carta.dataset.rareza === rareza;
			const coincideFaltan = !soloFaltan.checked || carta.dataset.obtenida === 'false';
			const visible = coincideRareza && coincideFaltan;
			carta.hidden = !visible;
			if (visible) visibles++;
		});
		vacio.hidden = visibles > 0;
	}

	botones.forEach(boton => boton.addEventListener('click', () => {
		botones.forEach(b => b.classList.remove('activo'));
		boton.classList.add('activo');
		rareza = boton.dataset.rareza;
		aplicarFiltro();
	}));

	soloFaltan.addEventListener('change', aplicarFiltro);
})();
