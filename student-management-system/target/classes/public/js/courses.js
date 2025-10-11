let courseSelected=null;
async function loadCourses(){
  try{
    const data = await API.get('/api/courses');
    const tbody = qs('#tbl tbody'); tbody.innerHTML='';
    for(const c of data){
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${c.id}</td><td>${c.code}</td><td>${c.title}</td><td>${c.credits}</td><td>${c.departmentId||''}</td>`;
      tr.onclick = ()=> selectCourse(c,tr);
      tbody.appendChild(tr);
    }
  }catch(e){ toast(e.message,'err'); }
}
function selectCourse(c,tr){
  qsa('#tbl tr').forEach(r=>r.classList.remove('sel'));
  tr.classList.add('sel');
  courseSelected = c.id;
  qs('#code').value = c.code||'';
  qs('#title').value = c.title||'';
  qs('#credits').value = c.credits||3;
  qs('#departmentId').value = c.departmentId||'';
  qs('#btnUpdate').disabled=false; qs('#btnDelete').disabled=false;
}
function readCourse(){
  return { code: qs('#code').value.trim(), title: qs('#title').value.trim(), credits: Number(qs('#credits').value)||3, departmentId: qs('#departmentId').value?Number(qs('#departmentId').value):null };
}
function clearCourse(){
  courseSelected=null; qsa('input').forEach(i=>i.value=''); qs('#credits').value=3; qs('#btnUpdate').disabled=true; qs('#btnDelete').disabled=true;
}
qs('#btnCreate').onclick = async ()=>{ try{ await API.post('/api/courses', readCourse()); toast('Created'); clearCourse(); loadCourses(); }catch(e){ toast(e.message,'err'); } };
qs('#btnUpdate').onclick = async ()=>{ if(!courseSelected) return toast('Select a course','err'); try{ await API.put(`/api/courses/${courseSelected}`, readCourse()); toast('Updated'); clearCourse(); loadCourses(); }catch(e){ toast(e.message,'err'); } };
qs('#btnDelete').onclick = async ()=>{ if(!courseSelected) return toast('Select a course','err'); if(!confirm('Delete selected course?')) return; try{ await API.del(`/api/courses/${courseSelected}`); toast('Deleted'); clearCourse(); loadCourses(); }catch(e){ toast(e.message,'err'); } };
qs('#btnClear').onclick = clearCourse;
loadCourses();
