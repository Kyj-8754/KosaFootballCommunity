   <template>
    <button class="scroll-to-top" @click="scrollToTop">⬆</button>
   </template>



   <style>
  .scroll-to-top {
position: fixed;
  bottom: 24px;
  right: 24px;
  background-color: #343a40;
  color: white;
  font-size: 20px;
  width: 50px;
  height: 50px;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  line-height: 1;
  padding: 0;
  transition: transform 0.2s;
}
  .scroll-to-top:hover {
    transform: scale(1.05);
  }
   </style>


   <script>
  export default {
  name: 'ScrollUp',
  methods: {
    scrollToTop() {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }
}
   </script>