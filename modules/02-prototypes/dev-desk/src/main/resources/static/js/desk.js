// Nạp phần thân của từng widget từ endpoint của nó và tự làm mới theo chu kỳ.
(() => {
  const bodies = [...document.querySelectorAll('.tool__body[data-src]')];

  async function load(body) {
    try {
      const res = await fetch(body.dataset.src, { headers: { 'X-Requested-With': 'fetch' } });
      if (!res.ok) throw new Error('HTTP ' + res.status);
      body.innerHTML = await res.text();
      body.dataset.loaded = 'true';
      body.removeAttribute('data-error');
    } catch (err) {
      body.dataset.error = 'true';
      if (!body.dataset.loaded) {
        body.textContent = 'Không tải được công cụ này. Kiểm tra app còn chạy rồi bấm Làm mới.';
      }
    }
  }

  bodies.forEach((body) => {
    load(body);
    const seconds = Number(body.dataset.refresh);
    if (seconds > 0) {
      setInterval(() => {
        if (!document.hidden) load(body);
      }, seconds * 1000);
    }
  });

  document.addEventListener('click', (event) => {
    const button = event.target.closest('[data-refresh-now]');
    if (!button) return;
    const body = button.closest('.tool').querySelector('.tool__body[data-src]');
    if (body) load(body);
  });
})();
