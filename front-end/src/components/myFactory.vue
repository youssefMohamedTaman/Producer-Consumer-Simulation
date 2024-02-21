<template>
    <div class="page-container">
      <div class="button-container">
        <button @click="handleButtonClick('addMachine')" :class="{'btn': isAdding}">+Machine</button>
        <button @click="handleButtonClick('addQueue')" :class="{'btn': isAdding2}">+Queue</button>
        <button @click="handleButtonClick('addConnection')" :class="{'btn': isSelecting}">
          <img :src="require('@/assets/connect.png')">Connect</button>
        <button @click="handleButtonClick('runAction')" :class="{'btn2': isRunning}">
          <img :src="require('@/assets/run.png')">Run</button>
        <button @click="handleButtonClick('stopAction')">
          <img :src="require('@/assets/stop.png')" >Stop</button>
        <button @click="handleButtonClick('replayAction')" :class="{'btn2': isReplaying}">
          <img :src="require('@/assets/replay.png')">Replay</button>
          <button @click="handleButtonClick('stopInput')">
          <img :src="require('@/assets/stopinput.png')">Input Stop</button>
        <button @click="handleButtonClick('clearAction')">
          <img :src="require('@/assets/clear2.png')" >Clear</button>
      </div>

      <div class="my-layer" ref="stage">
      </div>
    </div>
  </template>
  
  <script>
  import Konva from "konva";
  import axios from "axios";
  export default {
    data() {
      return {
        x: 2,
        y: window.innerHeight/3,
        shape: null,
        qId: 0,
        mId: 1,
        isSelecting: false,
        isReplaying: false,
        isRunning: false,
        isAdding: false,
        isAdding2: true,
        selected1: null,
        selected2: null,
        tempArrow: null,
        arrowStart: {},
        arrowEnd: {},
        list: [],
        network: {}, 
      };
    },
    mounted() {
      this.stage = new Konva.Stage({
        container: this.$refs.stage,
        width: this.$refs.stage.offsetWidth,
        height: this.$refs.stage.offsetHeight,
      });
      this.layer = new Konva.Layer();
      this.stage.add(this.layer);
      this.addQueue();
      
    this.stage.on('click', ()=>{
                const pos = this.stage.getPointerPosition();
                this.x = pos.x;
                this.y = pos.y;
                if(this.shape=="machine" && !this.isRunning)
                    this.addMachine();
                else if(this.shape=="queue" && !this.isRunning)
                    this.addQueue();
                this.shape=null;
            });
  },
    methods: {
        handleButtonClick(button) {
          switch (button) {
            case 'addMachine':
              this.isSelecting = this.isAdding2 = false;
              this.isAdding = !this.isAdding;
              this.shape="machine";
              break;
            case 'addQueue':
              this.isSelecting = this.isAdding = false;
              this.isAdding2 = !this.isAdding2;
              this.shape="queue";
              break;
            case 'addConnection':
              this.isAdding = this.isAdding2 = false;
              if(!this.isRunning){
                if(!this.isSelecting){
                  this.isSelecting = true;
                  this.arrowStart = this.arrowEnd = {};
                  this.selected1 = this.selected2 = null;
                }
                else
                  this.isSelecting = false;
              }
              break;
            case 'runAction':
              this.isRunning = true ;
              this.isSelecting = this.isAdding = this.isAdding2 = false;
              this.stop();
              this.run();
              break;
            case 'stopAction':
                this.stop();
              break;
            case 'replayAction':
                this.isSelecting = this.isAdding = this.isAdding2 = false;
                this.replay();
              break;
            case 'clearAction':
              this.clear();
              break;
            case 'stopInput':
              this.stopInput();
              break;
          }
        },
      addMachine() {
          var t = "M"+this.mId.toString();
          const group = new Konva.Group({
            id: t,
            x: this.x,
            y: this.y,
            draggable: true
          });

          const circle = new Konva.Circle({
            radius: 35,
            fill: 'white',
            stroke: 'black',
            strokeWidth: 1,
          });
          const text = new Konva.Text({
            text: t,
            fill: 'black',
            fontSize: 18,
            x:-12,
            y:-16
          });

          group.add(circle);
          group.add(text);
          group.on('click', () => this.handleShapeClick(group));
          if(this.isAdding){
            this.layer.add(group);
            this.stage.batchDraw();
            this.network[t] = [],
            this.mId++;
          }
          this.isAdding = false;
      },
      addQueue() {
          var t = "Q"+this.qId.toString();
          const group = new Konva.Group({
            id: t,
            x: this.x,
            y: this.y,
            draggable: true
          });

          const rect = new Konva.Rect({
            width: 80,
            height: 40,
            fill: 'yellow',
            stroke: 'black',
            strokeWidth: 1,
          });

          const text = new Konva.Text({
            text: t+"\nn = 0",
            fill: 'black',
            fontSize: 18,
            x:20,
            y:1
          });
          group.add(rect);
          group.add(text);
          group.on('click', () => this.handleShapeClick(group));

          if(this.isAdding2){
            this.layer.add(group);
            this.stage.batchDraw();
            this.network[t] = [],
            this.qId++;
          }
          this.isAdding2 = false;
      },
      startArrow() {
        const pos = this.stage.getPointerPosition();
        this.arrowStart = {
          x: pos.x,
          y: pos.y
        };
        this.arrowEnd = this.arrowStart;
        this.tempArrow = new Konva.Arrow({
          points: [this.arrowStart.x, this.arrowStart.y, this.arrowEnd.x, this.arrowEnd.y],
          stroke: 'black',
          fill: 'black',
          strokeWidth: 2,
          draggable: true,
        });
      },
      updateArrow() {
      const pos = this.stage.getPointerPosition();
      this.arrowEnd = {
        x: pos.x,
        y: pos.y
      };
      this.tempArrow.points([this.arrowStart.x, this.arrowStart.y, this.arrowEnd.x, this.arrowEnd.y]);
      this.layer.add(this.tempArrow);
      this.stage.batchDraw();
      },
      handleShapeClick(group) {
          if(this.isSelecting){
              if(this.selected1==null){
                  this.selected1=group;
                  this.startArrow();
              }
              else if(this.selected2==null && (this.selected1.id() !== group.id())){
                  this.selected2=group;
                  var temp ={
                    from: this.selected1.id(),
                    to:this.selected2.id(),
                  }
                  if(this.selected1.id()[0] != this.selected2.id()[0] && this.check(temp)==true){
                    this.updateArrow();
                    this.list.push(temp);
                    if(temp.from[0]==='M'){
                      this.network[temp.from].unshift(temp.to);
                    }
                    else{
                      this.network[temp.to].push(temp.from);
                    }
                  }
                  this.selected1 = null;
                  this.selected2 = null;
              }
          }  
      },
      check(temp) {
          for (var x of this.list) {
              if ((x.from === temp.from && (x.to === temp.to || temp.from[0]==='M')) || (x.from === temp.to && x.to === temp.from)){
                  return false;
              }
          }
          return true;
      },
      delay(ms) {
          return new Promise(resolve => setTimeout(resolve, ms));
        },
        async run() {
        console.log("Network: ",this.network);
        const url = `http://localhost:8080/pc`;
         await axios.post(url,{
             myNetwork: this.network
          }).then(response => {
            console.log("RUNNING SUCCESSFULLY ",response.data);
            this.isRunning = true;
          }).catch( error =>{
            alert(error);
          });
          while (this.isRunning){
            await axios.get(url).then(response =>{
                this.runUpdate(response.data);
                console.log(response.data);
            }).catch(error => {
              alert(error);
              this.isRunning = false;
            });
            await this.delay(500);
          }  
        },
      runUpdate(map){
        for(var k in map){
          var cur = this.layer.findOne('#'+k);
          if(cur.id()[0]==='Q'){
            cur.getChildren()[1].text(cur.id()+ "\nn = " + map[k].size.toString());
          }
          else{
            if(map[k].product!==null)
              cur.getChildren()[0].fill(map[k].product.color);
            else{
              cur.getChildren()[0].fill('white');
              cur.getChildren()[1].text(cur.id()+"\nR");
              continue;
            }
            cur.getChildren()[1].text(cur.id()+ "\n"+(map[k].serviceTime/1000).toString());
          }
        }
      },
      stop(){
        this.isRunning = false;
        this.isReplaying = false;
        const url = `http://localhost:8080/pc`;
        axios.put(url).then(response =>{
          console.log("Stoped Successfully "+response.data);
        }).catch(error =>{
          alert(error);
        });
      },
      replay(){
          this.isReplaying = !this.isReplaying;
          for(var x in this.network){
            const value = this.network[x];
            console.log(x, value);
          }
          var cur = this.layer.findOne('#Q0');
          console.log(cur);
          cur.getChildren()[0].fill('rgb(47, 138, 199)');
      },
      stopInput(){
        const url = `http://localhost:8080/pc/inputStop`;
        axios.put(url).then(response =>{
          console.log("Input Stoped Successfully "+response.data);
        }).catch(error =>{
          alert(error);
        });
      },
      clear(){
        this.stop();
        const url = `http://localhost:8080/pc`;
        axios.delete(url).then(response =>{
          console.log("cleared Successfully "+response.data);
        }).catch(error =>{
          alert(error);
        });
        this.layer.destroyChildren();
        this.isSelecting = this.isAdding = this.isAdding2 
        = this.isRunning = this.isReplaying = false;
        this.qId = 0;
        this.mId = 1;
        this.x=2;
        this.y=window.innerHeight/3;
        this.list = [];
        this.isAdding2 = true;
        this.addQueue();          
      },
    },
  };
  </script>
  
  <style scoped>
  .page-container {
    height: 85vh;
    width: 95vw;
    position: relative;
  }
  .button-container {
    width: 100%;
    position: relative;
    justify-content: center;
    align-items: center;
    padding: 20px;
    border: 2px;
    border-bottom-left-radius: 10px;
    border-bottom-right-radius: 10px;
    background-color: black;
    transform: translate(-0%, -60%);
  }
  .my-layer {
    width: 100%;
    height: 95%;
    position: relative;
    border-radius: 10px;
    background-color: #ccc;
    transform: translate(2%, -7.5%);
  }
  button {
    width: 160px;
    height: 50px;
    padding: 10px;
    margin: 5px;
    font-size: large;
    color: #fff;
    border: 1px;
    border-radius: 5px;
    background-color: rgb(108, 31, 179);
    cursor: pointer;
    transition: background-color 0.2s;
  }
  
  button:hover {
    background-color: rgb(67, 13, 117);
  }
  .btn {
    background-color: goldenrod;
  }
  .btn:hover{
    background-color: rgb(160, 121, 23);
  }
  .btn2{
    background-color: red;
  }
  .btn2:hover{
    background-color: rgb(178, 0, 0);
  }
  img{
    width: 30px;
    height: 30px;
    vertical-align: middle; 
    margin-right: 5px;
  }
  </style>
  