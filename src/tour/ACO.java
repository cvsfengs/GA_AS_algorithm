package tour;

import java.util.ArrayList;


public class ACO {
	
	/**
	 * 景点的列表
	 */
	ArrayList<Scene> sceneList;
	/**
	 * 游玩的天数 默认为3
	 */
	double upDays;
	/**
	 * 蚂蚁对象数组
	 */
	Ant[] ants;
	/**
	 * 蚂蚁的数量
	 */
	int antCount;
	/**
	 * 城市两两之间的信息素 公式中的tao
	 */
	double[] pheromone;
	/**
	 * 城市的数量
	 */
	int cityCount;
	/**
	 * 最优的路线
	 */
	int[] bestTour;
	/**
	 * 当前最优长度
	 */
	int bestLength;
	
	public ACO(){
		this.upDays = 3.0;
	}

	/**
	 * 初始化蚁群
	 * @param sceneList 景点的列表
	 * @param antCount 蚂蚁的数量
	 * @param upDays 游玩的天数
	 */
	public void init(ArrayList<Scene> sceneList, int antCount, double upDays) {
		this.sceneList = sceneList;
		this.antCount = antCount;
		this.upDays = upDays;
		ants = new Ant[antCount];
		cityCount = sceneList.size();
		//初始化信息素 默认为1
		pheromone = new double[cityCount];
		for (int i = 0; i < cityCount; i++) {
			pheromone[i] = 0.8;
		}
		bestLength = Integer.MIN_VALUE;
		bestTour = new int[cityCount];
		for (int i = 0; i < antCount; i++) {
			ants[i] = new Ant();
			ants[i].init(sceneList, upDays);
		}
	}

	/**
	 * 蚁群算法的运行入口
	 * @param maxgen 运行最大的代数
	 */
	public void run(int maxgen) {
		for (int gen = 0; gen < maxgen; gen++) {
//			System.out.println("gen:" + gen);
			//每一只蚂蚁的移动过程
			for (int i = 0; i < antCount; i++) {
//				System.out.println("gen: " + gen + " -- antId:" + i);
				//对该蚂蚁进行城市路线选择
				for (int j = 1; j < cityCount; j++) {
					//select需要增加一个返回值
					if(!ants[i].selectNextCity(j, pheromone)){
						break;
					}
				}
				//计算该蚂蚁爬过的路线总长度
				ants[i].calcTourLength(sceneList);
				//判断是否为最优路线
				if (ants[i].getLength() > bestLength) {
					//保存最优代
					bestLength = ants[i].getLength();
					System.out.println("第" + gen + "代, 蚂蚁" + i + "，发现新的解为：" + bestLength);
					for (int j = 0; j < cityCount; j++) {
						bestTour[j] = ants[i].getTour()[j];
						if(bestTour[j] != -1){
							System.out.print(sceneList.get(bestTour[j]).getCityName() + " ");
						}
					}
					System.out.println();
				}
			}
			//更新信息素
			updatePheromone();
			//蚂蚁重新初始化
			for (int i = 0; i < antCount; i++) {
				ants[i].init(sceneList, upDays);
			}
		}
		System.out.println("end");
	}

	/**
	 * 更新信息素,使用ant-cycle模型 <br/>
	 * 公式1: T_ij(t+1) = (1-r)*T_ij(t) + delta_T_ij(t) <br/>
	 * 公式2: delta_T_ij(t) = Q/L_k Q为常数，L_k为蚂蚁走过的总长度
	 */
	private void updatePheromone() {
		double rou = 0.01;
		for (int i = 0; i < cityCount; i++) {
			//信息素的衰减
			pheromone[i] *= (1 - rou);
		}
		for (int i = 0; i < antCount; i++) {
			for (int j = 0; j < cityCount; j++) {
				int curId = ants[i].getTour()[j];
				if(curId != -1){
					//如果改城市被访问过
					pheromone[curId] += 1.0 / ants[i].getLength();
				}else{
					return;
				}
			}
		}
	}

	/**
	 * 打印路径长度
	 */
	public void reportResult() {
		System.out.println("最优路径长度是" + bestLength);
		for (int j = 0; j < cityCount; j++) {
			if(bestTour[j] != -1){
				System.out.print(sceneList.get(bestTour[j]).getCityName() + " ");
			}else{
				return;
			}
		}
	}
}