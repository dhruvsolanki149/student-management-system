let offerSelected=null;
async function loadOfferings(){
  try{
    const data = await API.get('/api/offerings');
    const tbody = qs('#tbl tbody'); tbody.innerHTML='';
    for(const o of data){
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${o.id}</td><td>${o.courseId}</td><td>${o.semester}</td><td>${o.academicYear}</td><td>${o.sectionId||''}</td>`;
      tr.onclick = ()=> selectOffering(o,tr);
      tbody.appendChild(tr);
    }
  }catch(e){ toast(e.message,'err'); }
}
function selectOffering(o,tr){
  qsa('#tbl tr').forEach(r=>r.classList.remove('sel'));
  tr.classList.add('sel');
  offerSelected = o.id;
  qs('#courseId').value = o.courseId||'';
  qs('#semester').value = o.semester||'';
  qs('#academicYear').value = o.academicYear||'';
  qs('#sectionId').value = o.sectionId||'';
  qs('#btnUpdate').disabled=false; qs('#btnDelete').disabled=false;
}
function readOffering(){ return { courseId: Number(qs('#courseId').value), semester: Number(qs('#semester').value), academicYear: qs('#academicYear').value.trim(), sectionId: qs('#sectionId').value?Number(qs('#sectionId').value):null }; }
function clearOffering(){ offerSelected=null; qsa('input').forEach(i=>i.value=''); qs('#btnUpdate').disabled=true; qs('#btnDelete').disabled=true; }
qs('#btnCreate').onclick = async ()=>{ try{ await API.post('/api/offerings', readOffering()); toast('Created'); clearOffering(); loadOfferings(); }catch(e){ toast(e.message,'err'); } };
qs('#btnUpdate').onclick = async ()=>{ if(!offerSelected) return toast('Select offering','err'); try{ await API.put(`/api/offerings/${offerSelected}`, readOffering()); toast('Updated'); clearOffering(); loadOfferings(); }catch(e){ toast(e.message,'err'); } };
qs('#btnDelete').onclick = async ()=>{ if(!offerSelected) return toast('Select offering','err'); if(!confirm('Delete selected offering?')) return; try{ await API.del(`/api/offerings/${offerSelected}`); toast('Deleted'); clearOffering(); loadOfferings(); }catch(e){ toast(e.message,'err'); } };
qs('#btnClear').onclick = clearOffering;
loadOfferings();
