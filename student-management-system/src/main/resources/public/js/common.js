const API = {
  base: "",
  async get(p){ const r=await fetch(this.base+p); return handle(r); },
  async post(p,b){ const r=await fetch(this.base+p,{method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify(b)}); return handle(r); },
  async put(p,b){ const r=await fetch(this.base+p,{method:"PUT",headers:{"Content-Type":"application/json"},body:JSON.stringify(b)}); return handle(r); },
  async del(p){ const r=await fetch(this.base+p,{method:"DELETE"}); return handle(r); }
};
async function handle(resp){ if(resp.ok) return resp.json().catch(()=>({})); const m=await resp.json().catch(()=>({error:resp.statusText})); throw new Error(m.error||"Request failed"); }
const qs = s => document.querySelector(s);
const qsa = s => [...document.querySelectorAll(s)];
const toastEl = (()=>{ const el=document.createElement("div"); el.className="toast"; document.body.appendChild(el); return el; })();
function toast(msg,type="ok"){ toastEl.textContent=msg; toastEl.className=`toast show ${type}`; setTimeout(()=>toastEl.className="toast",2200); }
(function themeInit(){ const k="__mis_theme"; const apply=v=>document.documentElement.setAttribute("data-theme",v); const current=localStorage.getItem(k)||(window.matchMedia("(prefers-color-scheme: light)").matches?"light":"dark"); apply(current); window.toggleTheme=()=>{ const v=(document.documentElement.getAttribute("data-theme")==="light")?"dark":"light"; apply(v); localStorage.setItem(k,v); }; })();
