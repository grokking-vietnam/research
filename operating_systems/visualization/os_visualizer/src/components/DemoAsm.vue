<template>
  <div>  
    <div class="log" style="display:block;width:250px; height:500px; border: 1px solid black; position: absolute; padding:5px;">
      <div class="btn btn-primary" @click="nextState">>></div>
      <div class="btn btn-primary" @click="resetState">Reset</div>

      <p v-for="(state, index) in states" v-bind:key="state.label"  style="text-align:left">
        <span v-if='index == currentStateIndex' class="highlight">></span>
        {{state.label}}
      </p>
    </div>

    <div class="canvas" style="left: 300px;">
      <div v-for="obj in objects" v-bind:key="obj.id" v-bind:style="objectStyle(obj)">
        <div v-for="child in obj.children" v-bind:key="child.id" v-bind:style="objectStyle(child)">
          {{child.data}}
        </div>
      </div>
    </div>
  </div>
  
</template>

<script>
export default {
  name: 'DemoAsm',
  data () {
    return {
      objects: [
        {
          id: 'register_a',
          name: 'Register A',
          children: [
            {
              id: 'register_a.label',
              position: 'absolute',
              width: '100px',
              height: '20px',
              left: '0px',
              top: '0px',
              border: '1px solid black',
              data: 'Register A'
            },
            {
              id: 'register_a.wrapper',
              position: 'absolute',
              width: '100px',
              height: '50px',
              left: '0px',
              top: '0px',
              border: '1px solid black',
              data: ''
            },
            {
              id: 'register_a.ah_label',
              position: 'absolute',
              width: '50px',
              height: '20px',
              left: '0px',
              top: '20px',
              data: 'AH',
              background: 'green'
            },
            {
              id: 'register_a.ah',
              position: 'absolute',
              width: '50px',
              height: '50px',
              left: '0px',
              top: '40px',
              border: '1px solid black',
              data: '',
              background: 'green'
            },
            {
              id: 'register_a.al_label',
              position: 'absolute',
              width: '50px',
              height: '20px',
              left: '50px',
              top: '20px',
              data: 'AL',
              background: 'yellow'
            },
            {
              id: 'register_a.al',
              position: 'absolute',
              width: '50px',
              height: '50px',
              left: '50px',
              top: '40px',
              border: '1px solid black',
              data: '',
              background: 'yellow'
            }
          ]
        },

        {
          id: 'register_b',
          name: 'Register B',
          position: 'absolute',
          top: '100px',
          left: '0px',

          children: [
            {
              id: 'register_b.label',
              position: 'absolute',
              width: '100px',
              height: '20px',
              left: '0px',
              top: '0px',
              border: '1px solid black',
              data: 'Register B'
            },
            {
              id: 'register_b.wrapper',
              position: 'absolute',
              width: '100px',
              height: '50px',
              left: '0px',
              top: '0px',
              border: '1px solid black',
              data: ''
            },
            {
              id: 'register_b.bh_label',
              position: 'absolute',
              width: '50px',
              height: '20px',
              left: '0px',
              top: '20px',
              data: 'BH',
              background: 'green'
            },
            {
              id: 'register_b.bh',
              position: 'absolute',
              width: '50px',
              height: '50px',
              left: '0px',
              top: '40px',
              border: '1px solid black',
              data: '',
              background: 'green'
            },
            {
              id: 'register_b.bl_label',
              position: 'absolute',
              width: '50px',
              height: '20px',
              left: '50px',
              top: '20px',
              data: 'BL',
              background: 'yellow'
            },
            {
              id: 'register_b.bl',
              position: 'absolute',
              width: '50px',
              height: '50px',
              left: '50px',
              top: '40px',
              border: '1px solid black',
              data: '',
              background: 'yellow'
            }
          ]
        },

        {
          id: 'interrupt',
          name: 'Interrupt',
          children: [
            {
              id: 'interrupt.label',
              position: 'absolute',
              width: '100px',
              height: '20px',
              left: '150px',
              top: '0px',
              border: '1px solid black',
              data: 'Interrupt'
            },
            {
              id: 'interrupt.value',
              position: 'absolute',
              width: '100px',
              height: '50px',
              left: '150px',
              top: '20px',
              border: '1px solid black',
              data: ''
            }
          ]
        },

        {
          id: 'screen',
          name: 'Screen',
          left: '300px',
          top: '0px',
          position: 'absolute',

          children: [
            {
              id: 'screen.label',
              position: 'absolute',
              width: '100px',
              height: '20px',
              left: '0px',
              top: '0px',
              border: '1px solid black',
              data: 'Terminal'
            },
            {
              id: 'screen.value',
              position: 'absolute',
              width: '100px',
              height: '50px',
              left: '0px',
              top: '20px',
              border: '1px solid black',
              data: ''
            }
          ]
        }
      ],

      map: {},

      initState: {
        'register_a.ah': '',
        'register_a.al': '',
        'interrupt.value': '',
        'screen.value': ''
      },

      states: [
        {
          label: 'mov ah 0x0e;',
          setState: () => {
            this.getObject('register_a.ah').data = '0x0e';
          }
        },
        {
          label: 'mov al "H";',
          setState: () => {
            this.getObject('register_a.al').data = 'H';
          }
        },

        {
          label: 'int 0x10;',
          setState: () => {
            this.getObject('interrupt.value').data = '0x10';

            this.getObject('screen.value').data = this.getObject('screen.value').data + 'H';
          }
        },

        {
          label: 'mov al "E";',
          setState: () => {
            this.getObject('register_a.al').data = 'E';
          }
        },

        {
          label: 'int 0x10;',
          setState: () => {
            this.getObject('interrupt.value').data = '0x10';

            this.getObject('screen.value').data = this.getObject('screen.value').data + 'E';
          }
        },

        {
          label: 'mov al "L";',
          setState: () => {
            this.getObject('register_a.al').data = 'L';
          }
        },

        {
          label: 'int 0x10;',
          setState: () => {
            this.getObject('interrupt.value').data = '0x10';

            this.getObject('screen.value').data = this.getObject('screen.value').data + 'L';
          }
        },

        {
          label: 'int 0x10;  "l" is still on al, remember?',
          setState: () => {
            this.getObject('screen.value').data = this.getObject('screen.value').data + 'L';
          }
        },


        {
          label: 'mov al "O";',
          setState: () => {
            this.getObject('register_a.al').data = 'O';
          }
        },

        {
          label: 'int 0x10;',
          setState: () => {
            this.getObject('interrupt.value').data = '0x10';

            this.getObject('screen.value').data = this.getObject('screen.value').data + 'O';
          }
        },
      ],

      currentStateIndex: -1,
    }
  },
  methods: {
    objectStyle (childStyle) {
      return childStyle
    },

    initObjectMap () {
      for(var i=0; i<this.objects.length; i++) {
        var obj = this.objects[i];
        this.registerObject(obj);

        if(obj.children) {
          for(var j=0; j<obj.children.length; j++) {

            this.registerObject(obj.children[j]);
          }
        }
      }
    },

    registerObject (obj) {
      if(obj && obj.id && obj.id != '') {
        this.map[obj.id] = obj;
      }
    },

    getObject (id) {
      return this.map[id];
    },

    nextState () {
      this.currentStateIndex += 1;
      if(this.currentStateIndex > this.states.length-1) {
        this.currentStateIndex = this.states.length-1;
        return;
      }
      
      this.states[this.currentStateIndex].setState();
    },

    resetState () {
      console.log(Object.keys(this.initState));

      for(let key in this.initState) {
        console.log(key);
        if(key) {
          this.getObject(key).data = this.initState[key];
        }
        
      }

      this.currentStateIndex = -1;
    }
  },
  mounted () {
    this.initObjectMap();
    // this.nextState();
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1, h2 {
  font-weight: normal;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}


.canvas {
  display: block;
  position: absolute;
  width: 1024px;
  height: 600px;
  background: lightblue;
  border: 1px solid black;
}

.object {
  position: absolute;
  border: 1px solid black;
}

.highlight {
  background: yellow;
}
</style>
