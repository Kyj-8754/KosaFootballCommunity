   <template>
    <button class="scroll-to-top" @click="scrollToTop">⬆</button>
   </template>



   <style>
  .scroll-to-top {
position: fixed;
  bottom: 20px;
  right: 20px;
  background-color: white;
  color: black;
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
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
  line-height: 1;
  padding: 0;
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