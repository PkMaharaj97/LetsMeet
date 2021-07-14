package com.example.letsmeet

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

@Suppress("DEPRECATION")
class ViewPageAdapter(fm: FragmentManager):FragmentPagerAdapter(fm)
{

    override fun getItem(position: Int): Fragment
    { when (position) {
        0 -> { return TeamFragment()
        }
        1 -> { return ScheduleFragment()
        }
        2-> { return ChartFragment()
        }

        else -> return TeamFragment()

    }

    }

    override fun getCount(): Int
    {
        return 3

    }
}