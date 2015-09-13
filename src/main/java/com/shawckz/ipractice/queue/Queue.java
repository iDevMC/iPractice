package com.shawckz.ipractice.queue;

import com.shawckz.ipractice.Practice;
import com.shawckz.ipractice.match.Ladder;
import com.shawckz.ipractice.match.Match;
import com.shawckz.ipractice.player.IPlayer;
import com.shawckz.ipractice.queue.member.QueueMember;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by 360 on 9/12/2015.
 */
public abstract class Queue {

    private final QueueType type;

    public Queue(QueueType type) {
        this.type = type;
    }

    public QueueType getType() {
        return type;
    }

    @Getter private Set<QueueMember> members = new HashSet<>();

    public void run(){
        for(Ladder ladder : Ladder.getLadders()){
            Set<QueueMatchSet> found = findMatches(ladder);
            for(final QueueMatchSet set : found){
                for(QueueMember mem : set.getAll()){
                    if(members.contains(mem)){
                        members.remove(mem);
                    }
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(Practice.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        createMatch(set).startMatch(Practice.getMatchManager());
                    }
                });
            }
        }
    }

    public Set<QueueMatchSet> findMatches(Ladder ladder) {
        Iterator<QueueMember> it = getMembers().iterator();
        Set<QueueMatchSet> results = new HashSet<>();
        while(it.hasNext()){
            QueueMember search = it.next();
            if(it.hasNext()){
                QueueMember found = it.next();
                if(found.getLadder().getName().equals(search.getLadder().getName())){
                    QueueMatchSet set = new QueueMatchSet(ladder, search, found);
                    if(inRange(set)){
                        results.add(set);
                    }
                }
            }
        }
        return results;
    }

    public boolean inQueue(IPlayer player){
        for(QueueMember member : members){
            if(member.getPlayers().contains(player)){
                return true;
            }
        }
        return false;
    }

    public abstract void addToQueue(IPlayer player, Ladder ladder);

    public void removeFromQueue(QueueMember member) {
        if(getMembers().contains(member)){
            getMembers().remove(member);
        }
    }

    public abstract Match createMatch(QueueMatchSet set);

    public abstract Material getIcon();

    public abstract boolean canJoin(IPlayer player);

    public QueueMember getMember(IPlayer player) {
        for(QueueMember member : getMembers()){
            if(member.getPlayers().contains(player)){
                return member;
            }
        }
        return null;
    }

    public boolean inRange(QueueMatchSet set){
        return set.getAlpha().getRange().inRange(set.getBravo().getRange());
    }

}